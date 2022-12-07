package com.example.seckill.to;

import com.example.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-11-08 18:30
 */
@Data
public class SeckillSkuRedisTo {
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

	// sku 的详细信息
	private SkuInfoVo skuInfo;

	private Long startTime;

	private Long endTime;

	/**
	 * 随机码
	 */
	private String randomCode;
}
