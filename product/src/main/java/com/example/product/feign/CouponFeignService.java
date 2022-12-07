package com.example.product.feign;

import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author sally
 * @date 2022-09-13 3:53 PM
 */
@FeignClient("glm-coupon")
public interface CouponFeignService {
	/**
	 * 只要json数据模型是兼容的，双方服务无需使用同一个to
	 * @param spuBoundTo
	 * @return
	 */
	@PostMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

	@PostMapping("/coupon/skufullreduction/saveReduction")
	R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
