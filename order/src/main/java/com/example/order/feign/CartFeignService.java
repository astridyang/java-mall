package com.example.order.feign;

import com.example.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author sally
 * @date 2022-10-14 15:46
 */
@FeignClient("glm-cart")
public interface CartFeignService {
	@GetMapping("/currentUserCartItems")
	List<OrderItemVo> getCartItems();
}
