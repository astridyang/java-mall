package com.example.product.feign;

import com.example.common.to.SkuHasStockTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-20 9:56
 */
@FeignClient("glm-ware")
public interface WareFeignService {

	@PostMapping("/ware/waresku/hasStock")
	R hasStock(@RequestBody List<Long> skuIds);
}
