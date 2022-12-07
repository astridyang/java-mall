package com.example.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.cart.feign.ProductFeignService;
import com.example.cart.interceptor.CartInterceptor;
import com.example.cart.service.CartService;
import com.example.cart.to.UserInfoTo;
import com.example.cart.vo.Cart;
import com.example.cart.vo.CartItem;
import com.example.cart.vo.SkuInfoVo;
import com.example.common.constant.CartConstant;
import com.example.common.utils.R;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author sally
 * @date 2022-10-12 10:48
 */
@Service
public class CartServiceImpl implements CartService {
	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	ThreadPoolExecutor executor;

	@Resource
	ProductFeignService productFeignService;

	@Override
	public CartItem addToCart(Long skuId, int num) throws ExecutionException, InterruptedException {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		String result = (String) cartOps.get(skuId.toString());
		if (StringUtils.hasLength(result)) {
			// 购物车已经有商品，更新数量
			CartItem cartItem = JSON.parseObject(result, CartItem.class);
			cartItem.setCount(cartItem.getCount() + num);
			cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
			return cartItem;
		} else {
			CartItem cartItem = new CartItem();
			// 1.远程查询商品skuInfo
			CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
				R r = productFeignService.info(skuId);
				SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
				});
				cartItem.setSkuId(skuId);
				cartItem.setCheck(true);
				cartItem.setCount(num);
				cartItem.setTitle(skuInfo.getSkuTitle());
				cartItem.setPrice(skuInfo.getPrice());
				cartItem.setImage(skuInfo.getSkuDefaultImg());
			}, executor);

			// 2.远程查询商品销售属性字符串
			CompletableFuture<Void> getSaleAttrString = CompletableFuture.runAsync(() -> {
				List<String> attrString = productFeignService.getAttrString(skuId);
				cartItem.setSkuAttr(attrString);
			}, executor);

			CompletableFuture.allOf(getSkuInfo, getSaleAttrString).get();
			// 3.将商品保存到redis

			String s = JSON.toJSONString(cartItem);
			cartOps.put(skuId.toString(), s);

			return cartItem;
		}

	}

	@Override
	public CartItem getCartItem(Long skuId) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		String string = (String) cartOps.get(skuId.toString());
		if (StringUtils.hasLength(string)) {
			return JSON.parseObject(string, CartItem.class);
		}
		return null;
	}

	@Override
	public Cart getCart() throws ExecutionException, InterruptedException {
		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		Cart cart = new Cart();
		if (userInfoTo.getUserId() == null) {
			// 未登录
			String key = userInfoTo.getUserKey();
			List<CartItem> cartItems = getCartItems(CartConstant.CART_PREFIX + key);
			cart.setItems(cartItems);
			return cart;
		} else {
			// 已登录
			// 查询临时购物车
			Long id = userInfoTo.getUserId();
			String key = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
			List<CartItem> tempCartList = getCartItems(key);
			if (tempCartList != null) {
				// 合并购物车
				for (CartItem cartItem : tempCartList) {
					addToCart(cartItem.getSkuId(), cartItem.getCount());
				}
				// 删除临时购物车
				clearCart(key);
			}
			// 获取登录后的购物车（包含临时购物车数据）
			String key1 = CartConstant.CART_PREFIX + id;
			List<CartItem> cartItems = getCartItems(key1);
			cart.setItems(cartItems);
			return cart;
		}
	}

	private List<CartItem> getCartItems(String cartKey) {
		BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(cartKey);
		List<Object> values = ops.values();
		if (values != null && values.size() > 0) {
			List<CartItem> cartItems = values.stream().map(item -> {
				String obj = (String) item;
				return JSON.parseObject(obj, CartItem.class);
			}).collect(Collectors.toList());

			return cartItems;
		}
		return null;
	}

	@Override
	public void clearCart(String cartKey) {
		stringRedisTemplate.delete(cartKey);
	}

	@Override
	public void checkCartItem(Long skuId, Integer check) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		CartItem cartItem = getCartItem(skuId);
		cartItem.setCheck(check == 1);
		String s = JSON.toJSONString(cartItem);
		cartOps.put(skuId.toString(), s);
	}

	@Override
	public void changeItemCount(Long skuId, Integer num) {
		CartItem cartItem = getCartItem(skuId);
		cartItem.setCount(num);
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		String s = JSON.toJSONString(cartItem);
		cartOps.put(skuId.toString(), s);
	}

	@Override
	public void deleteItem(Long skuId) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		cartOps.delete(skuId.toString());
	}

	@Override
	public List<CartItem> currentUserCartItems() {

		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		if (userInfoTo.getUserId() == null) {
			return null;
		} else {
			String key = CartConstant.CART_PREFIX + userInfoTo.getUserId();
			List<CartItem> cartItems = getCartItems(key);
			assert cartItems != null;
			List<CartItem> collect = cartItems.stream()
					.peek(item -> {
						// 查询商品最新价格
						R r = productFeignService.getPrice(item.getSkuId());
						String data = (String) r.get("data");
						item.setPrice(new BigDecimal(data));
					})
					.filter(CartItem::isCheck).collect(Collectors.toList());
			return collect;
		}
	}

	private BoundHashOperations<String, Object, Object> getCartOps() {
		String key = "";
		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		if (userInfoTo.getUserId() != null) {
			key = CartConstant.CART_PREFIX + userInfoTo.getUserId();
		} else {
			key = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
		}
		return stringRedisTemplate.boundHashOps(key);
	}
}
