package com.example.auth.feign;

import com.example.auth.vo.LoginVo;
import com.example.auth.vo.RegisterVo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author sally
 * @date 2022-10-10 16:24
 */
@FeignClient("glm-member")
public interface MemberFeignService {
	@PostMapping("/member/member/register")
	R register(@RequestBody RegisterVo vo);

	@PostMapping("/member/member/login")
	R login(@RequestBody LoginVo vo);
}
