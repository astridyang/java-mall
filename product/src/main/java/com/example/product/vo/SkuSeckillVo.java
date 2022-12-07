package com.example.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-11-09 18:20
 */
@Data
public class SkuSeckillVo {
	/**
	 * 活动id
	 */
	private Long promotionId;
	/**
	 * 活动场次id
	 */
	private Long promotionSessionId;
	/**
	 * 商品id
	 */
	private Long skuId;
	/**
	 * 秒杀价格
	 */
	private BigDecimal seckillPrice;
	/**
	 * 秒杀总量
	 */
	private BigDecimal seckillCount;
	/**
	 * 每人限购数量
	 */
	private BigDecimal seckillLimit;
	/**
	 * 排序
	 */
	private Integer seckillSort;


	private Long startTime;

	private Long endTime;

	/**
	 * 随机码
	 */
	private String randomCode;
}
