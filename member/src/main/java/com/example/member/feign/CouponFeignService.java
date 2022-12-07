package com.example.member.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sally
 * @date 2022-07-13 2:02 PM
 */
@FeignClient("glm-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/coupon")
    R memberCoupons();
}
