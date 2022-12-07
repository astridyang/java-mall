package com.example.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.example.common.to.SkuEsModel;
import com.example.search.constant.EsConstant;
import com.example.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sally
 * @date 2022-09-20 11:03
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
	@Resource
	ElasticsearchClient esClient;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
		BulkRequest.Builder builder = new BulkRequest.Builder();
		for (SkuEsModel skuEsModel : skuEsModelList) {
			builder.operations(op -> op.index(idx -> idx.index(EsConstant.PRODUCT_INDEX).id(skuEsModel.getSkuId().toString()).document(skuEsModel)));
		}
		BulkResponse response = esClient.bulk(builder.build());
		boolean hasError = response.errors();
		if (hasError) {
			// 记录索引失败的id
			List<String> collect = response.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());
			log.error("商品上架es索引错误：{},返回数据: {}", collect, response);
		}else {
			log.info("商品上架完成");
		}
		return !hasError;
	}
}
