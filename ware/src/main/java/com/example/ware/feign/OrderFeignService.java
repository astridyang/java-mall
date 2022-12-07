package com.example.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author sally
 * @date 2022-10-21 17:05
 */
@FeignClient("glm-order")
public interface OrderFeignService {
	@GetMapping("/order/order/info/{orderSn}")
	R getOrderByOrderSn(@PathVariable("orderSn") String orderSn);
}
