package com.example.cart.controller;

import com.example.cart.interceptor.CartInterceptor;
import com.example.cart.service.CartService;
import com.example.cart.to.UserInfoTo;
import com.example.cart.vo.Cart;
import com.example.cart.vo.CartItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author sally
 * @date 2022-10-12 10:49
 */
@Controller
public class CartController {
	@Resource
	CartService cartService;

	@RequestMapping("/currentUserCartItems")
	@ResponseBody
	public List<CartItem> getCartItems(){
		return cartService.currentUserCartItems();
	}

	@GetMapping("cart.html")
	public String cartListPage(Model model) throws ExecutionException, InterruptedException {
		Cart cart = cartService.getCart();
		model.addAttribute("cart", cart);
		return "cartList";
	}

	@GetMapping("/deleteItem")
	public String deleteItem(@RequestParam("skuId") Long skuId) {
		cartService.deleteItem(skuId);
		return "redirect://cart.gmall.com/cart.html";
	}

	@GetMapping("/changeItemCount")
	public String changeItemCount(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
		cartService.changeItemCount(skuId, num);
		return "redirect://cart.gmall.com/cart.html";
	}

	@GetMapping("/addToCart")
	public String addToCart(@RequestParam("skuId") Long skuId,
	                        @RequestParam("num") int num,
	                        RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
		cartService.addToCart(skuId, num);
		redirectAttributes.addAttribute("skuId", skuId);
		return "redirect://cart.gmall.com/addToCartSuccess.html";
	}

	@GetMapping("/addToCartSuccess.html")
	public String addSuccess(@RequestParam("skuId") Long skuId, Model model) {
		CartItem cartItem = cartService.getCartItem(skuId);
		model.addAttribute("cartItem", cartItem);
		return "success";
	}

	@GetMapping("/checkCartItem")
	public String checkCartItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
		cartService.checkCartItem(skuId, check);
		return "redirect://cart.gmall.com/cart.html";
	}

}
