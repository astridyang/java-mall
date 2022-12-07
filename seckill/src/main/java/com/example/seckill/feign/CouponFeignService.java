package com.example.seckill.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author sally
 * @date 2022-11-08 15:00
 */
@FeignClient("glm-coupon")
public interface CouponFeignService {

	@GetMapping("/coupon/seckillsession/latest3DaySession")
	R getLatest3DaySession();
}
