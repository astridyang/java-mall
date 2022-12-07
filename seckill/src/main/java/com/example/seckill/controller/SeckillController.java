package com.example.seckill.controller;

import com.example.common.utils.R;
import com.example.seckill.service.SeckillService;
import com.example.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sally
 * @date 2022-11-09 17:07
 */
@Controller
public class SeckillController {
	@Resource
	SeckillService seckillService;

	@ResponseBody
	@GetMapping("/getCurrentSeckillSkus")
	public R getCurrentSeckillSkus() {
		List<SeckillSkuRedisTo> toList = seckillService.getCurrentSeckillSkus();
		return R.ok().setData(toList);
	}

	@ResponseBody
	@GetMapping("/sku/seckill/{skuId}")
	public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {

		try {
			Thread.sleep(300); // todo 测试降级熔断代码
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SeckillSkuRedisTo data = seckillService.getSkuSeckillInfo(skuId);
		return R.ok().setData(data);
	}

	@GetMapping("/kill")
	public String kill(@RequestParam("killId") String killId, @RequestParam("key") String key,
	                   @RequestParam("num") Integer num, Model model) {
		String orderSn = seckillService.kill(killId, key, num);
		model.addAttribute("orderSn", orderSn);
		return "success";
	}
}
