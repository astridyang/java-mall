package com.example.product.feign;

import com.example.common.to.SkuEsModel;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-20 11:33
 */
@FeignClient("glm-search")
public interface SearchFeignService {
	@PostMapping("/search/save/product")
	R productUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
