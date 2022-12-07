package com.example.search.service;

import com.example.common.to.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author sally
 * @date 2022-09-20 11:02
 */
public interface ProductSaveService {
	boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
