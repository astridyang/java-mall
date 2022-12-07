package com.example.product.web;

import com.example.product.service.SkuInfoService;
import com.example.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-10-08 15:54
 */
@Controller
public class ItemController {

	@Resource
	SkuInfoService skuInfoService;

	@GetMapping("/{skuId}.html")
	public String skuItem(@PathVariable Long skuId, Model model) {

		SkuItemVo vo = skuInfoService.item(skuId);
		model.addAttribute("item", vo);
		return "item";
	}
}
