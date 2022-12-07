package com.example.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-10-12 9:25
 */
public class CartItem {
	private Long skuId;
	private boolean check = true;
	private String title;
	private String image;
	private List<String> skuAttr;
	private BigDecimal price;
	private Integer count;
	private BigDecimal totalPrice;

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getSkuAttr() {
		return skuAttr;
	}

	public void setSkuAttr(List<String> skuAttr) {
		this.skuAttr = skuAttr;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTotalPrice() {
		return this.price.multiply(new BigDecimal("" + this.count));
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
}
