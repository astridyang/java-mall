package com.example.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.common.to.SkuEsModel;
import com.example.search.constant.EsConstant;
import com.example.search.service.MallSearchService;
import com.example.search.vo.SearchParam;
import com.example.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {
	@Resource
	private ElasticsearchClient esClient;

	@Override
	public SearchResult search(SearchParam param) {

		SearchResult result = null;
		try {
			/**
			 * 构建请求
			 * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
			 */
			//1. 构建bool-query
			BoolQuery.Builder boolBuilder = QueryBuilders.bool();

			String keyword = param.getKeyword();
			if (StringUtils.hasLength(keyword)) {
				//1.1 bool-must
				boolBuilder.must(q -> q.match(m -> m.field("skuTitle").query(FieldValue.of(keyword))));

			}
			//1.2 bool-filter
			if (param.getCatalog3Id() != null) {
				//1.2.1 catelogId
				boolBuilder.filter(q -> q.term(t -> t.field("catalogId").value(FieldValue.of(param.getCatalog3Id()))));
			}
			if (param.getBrandId() != null && param.getBrandId().size() > 0) {
				//1.2.2 brandId
				List<FieldValue> fieldValues = new ArrayList<>();
				param.getBrandId().forEach(item -> fieldValues.add(FieldValue.of(item)));
				boolBuilder.filter(q -> q.terms(t -> t.field("brandId").terms(t2 -> t2.value(fieldValues))));
			}

			//1.2.3 attrs=1_5寸:8寸&2_16G:8G
			if (param.getAttrs() != null && param.getAttrs().size() > 0) {
				param.getAttrs().forEach(item -> {
					BoolQuery.Builder attrBoolBuilder = QueryBuilders.bool();
					String[] s = item.split("_");
					String attrId = s[0];
					String[] attrValues = s[1].split(":");
					attrBoolBuilder.must(q -> q.term(t -> t.field("attrs.attrId").value(FieldValue.of(attrId))));
					List<FieldValue> collect = Arrays.stream(attrValues)
							.map(FieldValue::of)
							.collect(Collectors.toList());
					attrBoolBuilder.must(q -> q.terms(t -> t.field("attrs.attrValue").terms(t2 -> t2.value(collect))));
					NestedQuery.Builder nested = QueryBuilders.nested();
					nested.query(attrBoolBuilder.build()._toQuery()).path("attrs");
					boolBuilder.filter(nested.build()._toQuery());
				});
			}


			//1.2.4 hasStock
			if (param.getHasStock() != null) {
				boolBuilder.filter(q -> q.term(t -> t.field("hasStock").value(FieldValue.of(param.getHasStock() == 1))));
			}
			//1.2.5 skuPrice
			if (StringUtils.hasLength(param.getSkuPrice())) {
				String skuPrice = param.getSkuPrice();
				String[] price = skuPrice.split("_");
				if (skuPrice.startsWith("_")) {
					JsonData p = JsonData.of(price[1]);
					boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").lte(p)));
				} else if(skuPrice.endsWith("_")) {
					JsonData p = JsonData.of(price[0]);
					boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").gte(p)));
				}else{
					JsonData p1 = JsonData.of(price[0]);
					JsonData p2 = JsonData.of(price[1]);
					boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").gte(p1).lte(p2)));
				}
			}

			//封装所有的查询条件
			SearchRequest.Builder builder = new SearchRequest.Builder();
			builder.index(EsConstant.PRODUCT_INDEX).query(boolBuilder.build()._toQuery());

			/**
			 * 排序，分页，高亮
			 */
			//排序
			//形式为sort=hotScore_asc/desc
			String sort = param.getSort();
			if (StringUtils.hasLength(sort)) {
				String[] sorties = sort.split("_");
				SortOrder order = "asc".equalsIgnoreCase(sorties[1]) ? SortOrder.Asc : SortOrder.Desc;
				String field = sorties[0];
				if(Objects.equals(field, "price")){
					field = "skuPrice";
				}
				String finalField = field;
				builder.sort(s1 -> s1.field(f -> f.field(finalField).order(order)));
			}
			//分页
			builder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE).size(EsConstant.PRODUCT_PAGESIZE);
			//高亮

			if (StringUtils.hasLength(keyword)) {
				builder.highlight(h -> h.fields("skuTitle", b -> b.preTags("<b style='color:red'>").postTags("</b>")));
			}
			/**
			 * 聚合分析
			 */
			//1. 按照品牌进行聚合
			HashMap<String, Aggregation> aggregationHashMap = new HashMap<>();

			HashMap<String, Aggregation> brandAggMap = new HashMap<>();
			//1.1 品牌的子聚合-品牌名聚合
			Aggregation brandNameAgg = new Aggregation.Builder().terms(t -> t.field("brandName").size(1)).build();
			//1.2 品牌的子聚合-品牌图片聚合
			Aggregation brandImgAgg = new Aggregation.Builder().terms(t -> t.field("brandImg").size(1)).build();
			brandAggMap.put("brand_name", brandNameAgg);
			brandAggMap.put("brand_img", brandImgAgg);
			Aggregation brandAgg = new Aggregation.Builder().terms(new TermsAggregation.Builder().field("brandId").build()).aggregations(brandAggMap).build();
			aggregationHashMap.put("brand_agg", brandAgg);

			//2. 按照属性信息进行聚合
			HashMap<String, Aggregation> attrAggMap = new HashMap<>();
			//2.1.1 在每个属性ID下，按照属性名进行聚合
			HashMap<String, Aggregation> attrIdAggMap = new HashMap<>();
			Aggregation attrNameAgg = new Aggregation.Builder().terms(t -> t.field("attrs.attrName").size(1)).build();
			attrIdAggMap.put("attr_name_agg", attrNameAgg);

			//2.1.1 在每个属性ID下，按照属性值进行聚合
			Aggregation attrValueAgg = new Aggregation.Builder().terms(t -> t.field("attrs.attrValue").size(50)).build();
			attrIdAggMap.put("attr_value_agg", attrValueAgg);

			//2.1 按照属性ID进行聚合
			Aggregation attrIdAgg = new Aggregation.Builder().terms(t -> t.field("attrs.attrId").size(50)).aggregations(attrIdAggMap).build();
			attrAggMap.put("attr_id", attrIdAgg);
			Aggregation attrAgg = new Aggregation.Builder().nested(n -> n.path("attrs")).aggregations(attrAggMap).build();

			aggregationHashMap.put("attr_agg", attrAgg);

			//3. 按照分类信息进行聚合
			Aggregation catalogAgg = new Aggregation.Builder()
					.terms(t -> t.field("catalogId").size(50))
					.aggregations("catalog_name_agg", a -> a.terms(t -> t.field("catalogName").size(1))).build();
			aggregationHashMap.put("catalog_agg", catalogAgg);
			builder.aggregations(aggregationHashMap);


			SearchRequest request = builder.build();
			SearchResponse<SkuEsModel> response = esClient.search(request
					, SkuEsModel.class);


			result = buildSearchResult(response, param);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private SearchResult buildSearchResult(SearchResponse<SkuEsModel> response, SearchParam param) {
		SearchResult result = new SearchResult();
		//1、返回的所有查询到的商品
		List<SkuEsModel> products = new ArrayList<>();
		assert response.hits().total() != null;
		long totalHits = response.hits().total().value();
		if (totalHits > 0) {
			List<Hit<SkuEsModel>> hits = response.hits().hits();
			if (hits != null && hits.size() > 0) {
				for (Hit<SkuEsModel> hit : hits) {
					SkuEsModel esModel = hit.source();
					// System.out.println("source = " + source);
					//判断是否按关键字检索，若是就显示高亮，否则不显示
					if (StringUtils.hasLength(param.getKeyword())) {
						List<String> skuTitle = hit.highlight().get("skuTitle");
						String highlightTitle = skuTitle.get(0);
						assert esModel != null;
						esModel.setSkuTitle(highlightTitle);
					}
					products.add(esModel);
				}
			}
			result.setProducts(products);
		}
		// 聚合结果
		// 2.涉及到的所有品牌信息
		List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
		List<LongTermsBucket> brandAggBuckets = response.aggregations().get("brand_agg").lterms().buckets().array();
		for (LongTermsBucket bucket : brandAggBuckets) {
			// System.out.println("bucket = " + bucket);
			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
			//1、得到品牌的id
			String brandId = bucket.key();
			brandVo.setBrandId(Long.parseLong(brandId));
			//2、得到品牌的名字
			List<StringTermsBucket> brandNameList = bucket.aggregations().get("brand_name").sterms().buckets().array();
			String brandName = brandNameList.get(0).key();
			brandVo.setBrandName(brandName);
			//3、得到品牌的图片
			List<StringTermsBucket> brandImgList = bucket.aggregations().get("brand_img").sterms().buckets().array();
			String brandImg = brandImgList.get(0).key();
			brandVo.setBrandImg(brandImg);
			brandVoList.add(brandVo);
		}
		result.setBrands(brandVoList);

		// 3.涉及到的所有分类
		List<LongTermsBucket> catalogBuckets = response.aggregations().get("catalog_agg").lterms().buckets().array();
		List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
		for (LongTermsBucket bucket : catalogBuckets) {
			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
			//得到分类id
			String catalogId = bucket.key();
			catalogVo.setCatalogId(Long.parseLong(catalogId));
			//得到分类名
			List<StringTermsBucket> catalogNameAgg = bucket.aggregations().get("catalog_name_agg").sterms().buckets().array();
			catalogVo.setCatalogName(catalogNameAgg.get(0).key());

			catalogVoList.add(catalogVo);
		}
		result.setCatalogs(catalogVoList);


		// 4.涉及到的所有属性
		List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
		Aggregate attr_agg = response.aggregations().get("attr_agg").nested().aggregations().get("attr_id");
		List<LongTermsBucket> attrIdBuckets = attr_agg.lterms().buckets().array();
		for (LongTermsBucket bucket : attrIdBuckets) {
			SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
			//1、得到属性的id
			String attrId = bucket.key();
			attrVo.setAttrId(Long.parseLong(attrId));
			//2、得到属性的名字
			List<StringTermsBucket> attrNameBuckets = bucket.aggregations().get("attr_name_agg").sterms().buckets().array();
			attrVo.setAttrName(attrNameBuckets.get(0).key());
			//3、得到属性的所有值
			List<StringTermsBucket> attrValueBuckets = bucket.aggregations().get("attr_value_agg").sterms().buckets().array();
			List<String> values = attrValueBuckets.stream().map(StringTermsBucket::key).collect(Collectors.toList());
			attrVo.setAttrValue(values);

			attrVoList.add(attrVo);
		}
		result.setAttrs(attrVoList);

		//5、分页信息-页码
		result.setPageNum(param.getPageNum());
		//5、1分页信息、总记录数
		result.setTotal(totalHits);
		//5、2分页信息-总页码-计算
		int remain = (int) totalHits % EsConstant.PRODUCT_PAGESIZE;
		int pages = (int) totalHits / EsConstant.PRODUCT_PAGESIZE;
		int totalPages = remain == 0 ? pages : pages + 1;
		result.setTotalPages(totalPages);

		List<Integer> pageNavigation = new ArrayList<>();
		for (int i = 1; i <= totalPages; i++) {
			pageNavigation.add(i);
		}
		result.setPageNavigation(pageNavigation);

		return result;
	}
}