package com.example.order.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author sally
 * @date 2022-10-18 16:32
 */
@FeignClient("glm-product")
public interface ProductFeignService {
	@GetMapping("/product/spuinfo/skuId/{id}")
	R getSpuBySkuId(@PathVariable("id") Long id);
}
