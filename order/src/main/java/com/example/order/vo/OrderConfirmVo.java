package com.example.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author sally
 * @date 2022-10-14 14:59
 */
public class OrderConfirmVo {
	@Getter
	@Setter
	List<MemberAddressVo> address;
	@Getter
	@Setter
	List<OrderItemVo> items;
	// 优惠券
	@Getter
	@Setter
	Integer integration;

	@Getter
	@Setter
	Map<Long, Boolean> stocks;

	@Getter @Setter
	String orderToken;

	public Integer getCount() {
		int sum = 0;
		if (items != null) {
			for (OrderItemVo item : items) {
				sum += item.getCount();
			}
		}
		return sum;
	}

	// BigDecimal total; // 订单总额

	public BigDecimal getTotal() {
		BigDecimal sum = new BigDecimal("0");
		if (items != null) {
			for (OrderItemVo item : items) {
				sum = sum.add(item.getPrice().multiply(new BigDecimal(item.getCount() + "")));
			}
		}
		return sum;
	}

	// BigDecimal payPrice; // 应付价格

	public BigDecimal getPayPrice() {
		return getTotal();
	}
	// 防重令牌
}
