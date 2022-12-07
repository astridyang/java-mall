package com.example.order.web;

import com.example.order.service.OrderService;
import com.example.order.vo.OrderConfirmVo;
import com.example.order.vo.OrderSubmitVo;
import com.example.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author sally
 * @date 2022-10-14 14:13
 */
@Controller
public class OrderWebController {

	@Resource
	OrderService orderService;

	@PostMapping("/submitOrder")
	public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes) {
		// 创建订单，验证令牌，验价格，锁库存
		SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
		Integer code = responseVo.getCode();
		if (responseVo.getCode() == 0) {
			// if success,to pay
			model.addAttribute("submitOrderResp", responseVo);
			return "pay";
		} else {
			String msg = "submit order failed.";
			switch (code) {
				case 1:
					msg += "防重令牌校验失败";
					break;
				case 2:
					msg += "商品价格发生变化";
					break;
			}
			attributes.addFlashAttribute("msg", msg);
			return "redirect://order.gmall.com/toTrade";
		}
	}

	@GetMapping("/toTrade")
	public String confirmOrder(Model model) throws ExecutionException, InterruptedException {
		OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
		model.addAttribute("orderConfirmData", orderConfirmVo);
		return "confirm";
	}

}
