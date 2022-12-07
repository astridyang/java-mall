package com.example.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.JsonData;
import com.example.common.to.SkuEsModel;
import com.example.search.constant.EsConstant;
import com.example.search.vo.SearchParam;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
class SearchApplicationTests {

	@Resource
	private ElasticsearchClient esClient;



	@Test
	public void testGlmProductSearch() {
		SearchParam param = new SearchParam();
		param.setKeyword("apple"); // 🆗
		// param.setCatelog3Id(225L); // 🆗
		// param.setHasStock(1); // 🆗
		param.setSkuPrice("_9000"); // 🆗
		param.setSort("price_desc");
		// param.setBrandId(Arrays.asList(5L, 6L));// 🆗
		// param.setAttrs(Arrays.asList("3_Apple:aa"));// 🆗
		param.setPageNum(1); // 🆗
		SearchResponse<SkuEsModel> response = null;
		try {
			// SkuEsModel(skuId=7, spuId=9, skuTitle=Apple iPhone 13 Pro 红 128GB支持移动联通电信5G 双卡双待手机, skuPrice=7999.0000, skuImg=http://192.168.62.31:9010/glmall/glmall_1663061917018_2022-09-13_988.jpg, saleCount=0, hasStock=true, hotScore=0, brandId=3, brandName=Apple, brandImg=http://192.168.62.31:9010/glmall/glmall_1661133896873_2022-08-22_216.png, catalogId=225, catalogName=手机, attrs=[SkuEsModel.Attrs(attrId=3, attrName=CPU品牌, attrValue=Apple), SkuEsModel.Attrs(attrId=7, attrName=CPU型号, attrValue=A15)])
			/**
			 * 构建请求
			 * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
			 */
			//1. 构建bool-query
			// BoolQuery.Builder boolQuery = new BoolQuery.Builder();
			BoolQuery.Builder boolBuilder = QueryBuilders.bool();

			String keyword = param.getKeyword();
			if (StringUtils.hasLength(keyword)) {
				// Query byKeyword = MatchQuery.of(m -> m
				// 		.field("skuTitle")
				// 		.query(FieldValue.of(param.getKeyword()))
				// )._toQuery();
				// boolBuilder.must(byKeyword);
				//1.1 bool-must
				boolBuilder.must(q -> q.match(m -> m.field("skuTitle").query(FieldValue.of(keyword))));

			}
			//1.2 bool-filter
			if (param.getCatalog3Id() != null) {
				// Query byCatelog3Id = MatchQuery.of(m -> m
				// 		.field("catalogId")
				// 		.query(FieldValue.of(param.getCatelog3Id()))
				// )._toQuery();
				// boolBuilder.filter(byCatelog3Id);
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
				if (price.length == 2) {
					JsonData p1 = JsonData.of(price[0]);
					JsonData p2 = JsonData.of(price[1]);
					boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").gte(p1).lte(p2)));
				} else {
					if (skuPrice.startsWith("_")) {
						JsonData p = JsonData.of(price[1]);
						boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").lte(p)));
					} else {
						JsonData p = JsonData.of(price[0]);
						boolBuilder.filter(q -> q.range(r -> r.field("skuPrice").gte(p)));
					}
				}
			}

			//封装所有的查询条件
			SearchRequest.Builder builder = new SearchRequest.Builder();
			builder.index(EsConstant.PRODUCT_INDEX).query(boolBuilder.build()._toQuery());

			/**
			 * 排序，分页，高亮
			 */
			//排序
			//形式为sort=hotScore_asc/desc TODO keyword排序不准确，重建索引为long？
			String sort = param.getSort();
			if (StringUtils.hasLength(sort)) {
				String[] sorties = sort.split("_");
				SortOrder order = "asc".equalsIgnoreCase(sorties[1]) ? SortOrder.Asc : SortOrder.Desc;
				builder.sort(s1 -> s1.field(f -> f.field(sorties[0]).order(order)));
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

			// builder.aggregations("brand_agg", a -> a.terms(t -> t.field("brandId").size(50)));

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
			Aggregation catalogAgg = new Aggregation.Builder().terms(t -> t.field("catalogId").size(50)).build();
			aggregationHashMap.put("catalog_agg", catalogAgg);
			builder.aggregations(aggregationHashMap);
			SearchRequest request = builder.build();
			response = esClient.search(request
					, SkuEsModel.class);


			List<Hit<SkuEsModel>> hits = response.hits().hits();
			for (Hit<SkuEsModel> hit : hits) {
				SkuEsModel source = hit.source();
				// System.out.println("source = " + source);
				//判断是否按关键字检索，若是就显示高亮，否则不显示
				if (StringUtils.hasLength(keyword)) {
					List<String> skuTitle = hit.highlight().get("skuTitle");
					String highlightTitle = skuTitle.get(0);
					System.out.println("highlightTitle = " + highlightTitle);
				}
			}
			// 聚合
			List<LongTermsBucket> brandAggBuckets = response.aggregations().get("brand_agg").lterms().buckets().array();
			Aggregate attr_agg = response.aggregations().get("attr_agg").nested().aggregations().get("attr_id");
			List<LongTermsBucket> attrIdBuckets = attr_agg.lterms().buckets().array();
			for (LongTermsBucket attrIdBucket : attrIdBuckets) {
				List<StringTermsBucket> attrNameBuckets = attrIdBucket.aggregations().get("attr_name_agg").sterms().buckets().array();
				System.out.println("attrNameBuckets = " + attrNameBuckets);
				List<StringTermsBucket> attrValueBuckets = attrIdBucket.aggregations().get("attr_value_agg").sterms().buckets().array();
				System.out.println("attrValueBuckets = " + attrValueBuckets);
			}
			List<LongTermsBucket> catalog_agg = response.aggregations().get("catalog_agg").lterms().buckets().array();
			// for (LongTermsBucket brandAggBucket : brandAggBuckets) {
			// 	System.out.println("brandAggBucket = " + brandAggBucket);
			// 	List<StringTermsBucket> brandNameList = brandAggBucket.aggregations().get("brand_name").sterms().buckets().array();
			// 	List<StringTermsBucket> brandImgList = brandAggBucket.aggregations().get("brand_img").sterms().buckets().array();
			// 	System.out.println(brandNameList);
			// 	System.out.println(brandImgList);
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void testTermsAgg() throws IOException {
		// Map<String, Aggregation> map = new HashMap<>();
		//
		// Aggregation subAggregation = new Aggregation.Builder()
		// 		.avg(new AverageAggregation.Builder().field("revenue").build())
		// 		.build();
		//
		// Aggregation aggregation = new Aggregation.Builder()
		// 		.terms(new TermsAggregation.Builder().field("director.keyword").build())
		// 		.aggregations(new HashMap<>() {{
		// 			put("avg_renevue", subAggregation);
		// 		}}).build();
		//
		// map.put("agg_director", aggregation);
		String searchText = "bike";
		Query query = MatchQuery.of(m -> m.field("name").query(FieldValue.of(searchText)))._toQuery();
		// TermsAggregation.Builder builder = new TermsAggregation.Builder();
		// builder.field("price").size(10);
		SearchResponse<Void> response = esClient.search(s -> s
				.index("products")
				.size(0)
				.query(query)
				.aggregations("price-agg", a -> a.terms(t -> t.field("price").size(50))), Void.class);
		System.out.println("response = " + response);

	}

	@Test
	public void testAgg() throws IOException {
		String searchText = "bike";
		Query query = MatchQuery.of(m -> m.field("name").query(FieldValue.of(searchText)))._toQuery();
		SearchResponse<Void> response = esClient.search(s -> s
				.index("products")
				.size(0)
				.query(query)
				.aggregations("price-avg",
						a -> a.avg(av -> av.field("price"))), Void.class);
		AvgAggregate avg = response.aggregations().get("price-avg").avg();
		System.out.println(avg.value()); // 1807.6666666666667
	}

	@Test
	public void testSearch() throws IOException {
		// POST http://192.168.62.31:9200/products/_search?typed_keys=true
		String searchText = "bike";
		SearchResponse<Product> response = esClient.search(s -> s.index("products").query(q -> q.match(t -> t.field("name").query(FieldValue.of(searchText)))), Product.class);

		TotalHits total = response.hits().total();
		boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
		// there are 3 results.
		// Found product city bike, score 0.13353139
		// Found product mountain bike, score 0.13353139
		// Found product moto bike, score 0.13353139
		if (isExactResult) {
			System.out.println("there are " + total.value() + " results.");
		} else {
			System.out.println("there are more than " + total.value() + " results.");
		}
		List<Hit<Product>> hits = response.hits().hits();
		for (Hit<Product> hit : hits) {
			Product product = hit.source();
			assert product != null;
			System.out.println("Found product " + product.getName() + ", score " + hit.score());
		}

	}

	@Test
	void contextLoads() {
		// System.out.println(client);
		System.out.println(esClient);
	}

	@Test
	public void testClient2() throws IOException {
		Product product = new Product();
		product.setId(1L);
		product.setName("city bike");
		product.setPrice(new BigDecimal("123.0"));
		// esClient.index(i->i.index("products").id("1").document(product));
		esClient.indices().create(c -> c.index("products"));

	}

	@Test
	public void testIndex() throws IOException {
		Product product = new Product();
		product.setId(1L);
		product.setName("city bike");
		product.setPrice(new BigDecimal("123.0"));
		esClient.index(i -> i.index("products").id(String.valueOf(product.getId())).document(product));

	}

	@Test
	public void testGet() throws IOException {
		// get http://192.168.62.31:9200/products/_doc/1
		GetResponse<Product> response = esClient.get(g -> g.index("products").id("1"), Product.class);
		if (response.found()) {
			Product product = response.source();
			assert product != null;
			System.out.println("product.getName() = " + product.getName());
		} else {
			System.out.println("product not found");
		}
	}

	@Test
	public void testBulk() throws IOException {
		// POST http://192.168.62.31:9200/_bulk
		Product product1 = new Product();
		product1.setId(2L);
		product1.setName("mountain bike");
		product1.setPrice(new BigDecimal("300"));
		Product product2 = new Product();
		product2.setId(3L);
		product2.setName("moto bike");
		product2.setPrice(new BigDecimal("5000"));
		List<Product> productList = new ArrayList<>();
		productList.add(product1);
		productList.add(product2);
		BulkRequest.Builder builder = new BulkRequest.Builder();
		for (Product product : productList) {
			builder.operations(op -> op.index(idx -> idx.index("products").id(String.valueOf(product.getId())).document(product)));
		}

		BulkResponse response = esClient.bulk(builder.build());
		if (response.errors()) {
			System.out.println("bulk had errors");
			for (BulkResponseItem item : response.items()) {
				if (item.error() != null) {
					System.out.println(item.error().reason());
				}
			}
		}

	}


	@Data
	static
	class Product {
		private Long id;
		private String name;
		private BigDecimal price;
	}


}
