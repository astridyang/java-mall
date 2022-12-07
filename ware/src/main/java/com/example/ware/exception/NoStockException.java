package com.example.ware.exception;

/**
 * @author sally
 * @date 2022-10-21 14:21
 */
public class NoStockException extends RuntimeException{
	private Long skuId;
	public NoStockException(Long skuId){
		super("Goods id: " + skuId + " didn't has enough stock.");
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
}
