package com.example.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-10-12 9:25
 */
public class Cart {
	private List<CartItem> items;
	private Integer totalCount; // 所有商品数量
	private Integer totalType; // 商品种类数量
	private BigDecimal totalAmount;
	private BigDecimal reduce = new BigDecimal("0"); // 优惠

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Integer getTotalCount() {
		int count = 0;
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				count += item.getCount();
			}
		}
		return count;
	}


	public Integer getTotalType() {
		return items == null ? 0 : items.size();
	}

	public BigDecimal getTotalAmount() {
		BigDecimal amount = new BigDecimal("0");
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				if(item.isCheck()){
					amount = amount.add(item.getTotalPrice());
				}
			}
		}
		amount = amount.subtract(reduce);
		return amount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getReduce() {
		return reduce;
	}

	public void setReduce(BigDecimal reduce) {
		this.reduce = reduce;
	}
}
