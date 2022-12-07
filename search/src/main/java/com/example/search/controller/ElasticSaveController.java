package com.example.search.controller;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.SkuEsModel;
import com.example.common.utils.R;
import com.example.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author sally
 * @date 2022-09-20 10:59
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {
	@Resource
	ProductSaveService productSaveService;

	@PostMapping("/product")
	public R productUp(@RequestBody List<SkuEsModel> skuEsModelList) {
		boolean result = false;
		try {
			result = productSaveService.productStatusUp(skuEsModelList);
		} catch (IOException e) {
			log.error("ElasticSaveController商品上架错误: {}", e);
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}
		if (result) {
			return R.ok();
		} else {
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}
	}
}
