package com.example.cart.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-10-12 14:24
 */
@FeignClient("glm-product")
public interface ProductFeignService {
	@RequestMapping("/product/skuinfo/info/{id}")
	R info(@PathVariable("id") Long id);


	@GetMapping("/product/skusaleattrvalue/attrString/{skuId}")
	List<String> getAttrString(@PathVariable("skuId") Long skuId);

	@GetMapping("/product/skuinfo/{skuId}/price")
	R getPrice(@PathVariable("skuId") Long skuId);

}
