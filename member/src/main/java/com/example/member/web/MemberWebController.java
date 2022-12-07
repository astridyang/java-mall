package com.example.member.web;

import com.example.common.utils.R;
import com.example.member.feign.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sally
 * @date 2022-11-07 11:33
 */
@Controller
public class MemberWebController {
	@Resource
	OrderFeignService orderFeignService;

	@RequestMapping("/memberOrder.html")
	public String memberOrder(@RequestParam(value = "pageNum",defaultValue = "1") String pageNum, Model model){
		Map<String, Object> params = new HashMap<>();
		params.put("page",pageNum);
		R r = orderFeignService.listWithItem(params);
		model.addAttribute("orders",r);
		System.out.println(r);
		//
		return "orderList.html";
	}
}
