package com.example.seckill.service;

import com.example.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author sally
 * @date 2022-11-08 14:59
 */
public interface SeckillService {
	void upSeckillSkuLatest3Days();

	List<SeckillSkuRedisTo> getCurrentSeckillSkus();

	SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

	String kill(String killId, String key, Integer num);
}
