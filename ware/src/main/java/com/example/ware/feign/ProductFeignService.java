package com.example.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sally
 * @date 2022-09-15 10:23
 */
@FeignClient("glm-product")
public interface ProductFeignService {
	// 过网关写法，glm-gateway, /api/xxx
	// 不过网关
	@RequestMapping("/product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);
}
