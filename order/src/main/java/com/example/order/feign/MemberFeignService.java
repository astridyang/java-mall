package com.example.order.feign;

import com.example.common.utils.R;
import com.example.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author sally
 * @date 2022-10-14 15:40
 */
@FeignClient("glm-member")
public interface MemberFeignService {
	@GetMapping("/member/memberreceiveaddress/{memberId}/addressList")
	List<MemberAddressVo> getMemberAddress(@PathVariable("memberId") Long memberId);
}
