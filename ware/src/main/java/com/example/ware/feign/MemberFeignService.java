package com.example.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sally
 * @date 2022-10-18 15:34
 */
@FeignClient("glm-member")
public interface MemberFeignService {
	@RequestMapping("/member/memberreceiveaddress/info/{id}")
	R info(@PathVariable("id") Long id);
}
