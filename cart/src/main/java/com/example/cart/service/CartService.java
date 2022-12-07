package com.example.cart.service;

import com.example.cart.vo.Cart;
import com.example.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author sally
 * @date 2022-10-12 10:48
 */
public interface CartService {
	CartItem addToCart(Long skuId, int num) throws ExecutionException, InterruptedException;

	CartItem getCartItem(Long skuId);

	Cart getCart() throws ExecutionException, InterruptedException;

	void clearCart(String cartKey);

	void checkCartItem(Long skuId, Integer check);

	void changeItemCount(Long skuId, Integer num);

	void deleteItem(Long skuId);

	List<CartItem> currentUserCartItems();
}
