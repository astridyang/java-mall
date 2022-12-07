package com.example.auth.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sally
 * @date 2022-10-10 9:36
 */
@FeignClient("glm-third-party")
public interface ThirdPartyFeignService {

	@GetMapping("/sms/sendCode")
	R sentCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
