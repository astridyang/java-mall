package com.example.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.constant.OrderMq;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.common.utils.R;
import com.example.common.vo.MemberResVo;
import com.example.seckill.feign.CouponFeignService;
import com.example.seckill.feign.ProductFeignService;
import com.example.seckill.interceptor.LoginInterceptor;
import com.example.seckill.service.SeckillService;
import com.example.seckill.to.SeckillSkuRedisTo;
import com.example.seckill.vo.SeckillSessionVo;
import com.example.seckill.vo.SeckillSkuVo;
import com.example.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sally
 * @date 2022-11-08 14:59
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
	@Resource
	CouponFeignService couponFeignService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	RedissonClient redissonClient;

	@Resource
	RabbitTemplate rabbitTemplate;


	private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
	private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
	private final String SKU_STOCK_SEMAPHOR = "seckill:stock:";

	@Override
	public void upSeckillSkuLatest3Days() {
		R r = couponFeignService.getLatest3DaySession();
		if (r.getCode() == 0) {
			List<SeckillSessionVo> sessionList = r.getData(new TypeReference<List<SeckillSessionVo>>() {
			});
			System.out.println("sessionList = " + sessionList);
			// 缓存到redis
			// 1.缓存活动信息
			saveSessionInfo(sessionList);
			// 2.缓存活动的关联商品信息
			saveSessionSkuInfo(sessionList);

		}
	}

	public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
		log.error("资源被限流:getCurrentSeckillSkusResource...");
		return null;
	}

	@SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
	@Override
	public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
		long now = new Date().getTime();
		try (Entry entry = SphU.entry("seckillSkus")) {
			Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
			assert keys != null;
			for (String key : keys) {
				String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
				String[] s = replace.split("_");
				long start = Long.parseLong(s[0]);
				long end = Long.parseLong(s[1]);

				if (now >= start && now <= end) {
					List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
					BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
					assert range != null;
					List<String> skuList = hashOps.multiGet(range);
					if (skuList != null && skuList.size() > 0) {
						List<SeckillSkuRedisTo> collect = skuList.stream().map(item -> {
							SeckillSkuRedisTo skuRedisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
							return skuRedisTo;
						}).collect(Collectors.toList());
						return collect;
					}
					break;
				}
			}
			return null;
		} catch (BlockException e) {
			log.info("资源被限流：getCurrentSeckillSkus....");
		}


		return null;
	}

	@Override
	public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
		BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
		Set<String> keys = hashOps.keys();
		if (keys != null && keys.size() > 0) {
			String regx = "\\d_" + skuId;
			for (String key : keys) {
				if (Pattern.matches(regx, key)) {
					String json = hashOps.get(key);
					SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);

					long now = new Date().getTime();
					assert skuRedisTo != null;
					if (now >= skuRedisTo.getStartTime() && now <= skuRedisTo.getEndTime()) {

					} else {
						skuRedisTo.setRandomCode(null);
					}

					return skuRedisTo;
				}
			}
		}
		return null;
	}

	@Override
	public String kill(String killId, String key, Integer num) {
		long s1 = System.currentTimeMillis();
		MemberResVo member = LoginInterceptor.threadLocal.get();
		BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
		String json = hashOps.get(killId);
		if (StringUtils.hasLength(json)) {
			SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, new TypeReference<SeckillSkuRedisTo>() {
			});
			// 验证合法性
			Long start = skuRedisTo.getStartTime();
			Long end = skuRedisTo.getEndTime();
			long now = new Date().getTime();
			long ttl = end - now;
			if (now >= start && now <= end) {
				String randomCode = skuRedisTo.getRandomCode();
				if (randomCode.equals(key)) {
					if (num <= skuRedisTo.getSeckillLimit().intValue()) {
						// 验证用户是否买过，秒杀成功占位 userId_sessionId_skuId
						String redis_key = member.getId() + "_" + killId;
						// 自动过期
						Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redis_key, num.toString(), ttl, TimeUnit.MILLISECONDS);
						if (Boolean.TRUE.equals(aBoolean)) {
							// 没买过
							RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHOR + randomCode);
							boolean b = semaphore.tryAcquire(num);
							if (b) {
								// 秒杀成功，快速下单，给mq发信息让订单服务监听并创建保存订单
								String orderSn = IdWorker.getTimeId();
								SeckillOrderTo order = new SeckillOrderTo();
								order.setOrderSn(orderSn);
								order.setMemberId(member.getId());
								order.setSeckillPrice(skuRedisTo.getSeckillPrice());
								order.setNum(num);
								order.setPromotionSessionId(skuRedisTo.getPromotionSessionId());
								order.setSkuId(skuRedisTo.getSkuId());
								rabbitTemplate.convertAndSend(OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_SECKILL_KEY, order);
								long s2 = System.currentTimeMillis();
								log.info("seckill create order use {} milliseconds.", (s2 - s1));
								// 生成订单号
								return orderSn;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private void saveSessionInfo(List<SeckillSessionVo> sessionList) {
		if (sessionList != null) {
			sessionList.forEach(session -> {
				long start = session.getStartTime().getTime();
				long end = session.getEndTime().getTime();
				String key = SESSIONS_CACHE_PREFIX + start + "_" + end;
				if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
					List<String> collect = session.getRelationSkus().stream().map(sku -> sku.getPromotionSessionId() + "_" + sku.getSkuId().toString()).collect(Collectors.toList());
					stringRedisTemplate.opsForList().leftPushAll(key, collect);
				}
			});
		}
	}

	private void saveSessionSkuInfo(List<SeckillSessionVo> sessionList) {
		if (sessionList != null) {
			sessionList.forEach(session -> {
				BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);


				session.getRelationSkus().forEach(sku -> {
					if (Boolean.FALSE.equals(ops.hasKey(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId()))) {
						// 缓存商品
						SeckillSkuRedisTo to = new SeckillSkuRedisTo();
						// 1.sku 基本信息
						R r = productFeignService.getSkuInfo(sku.getSkuId());
						if (r.getCode() == 0) {
							SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
							});
							to.setSkuInfo(skuInfo);
						}
						// 2.sku 秒杀信息
						BeanUtils.copyProperties(sku, to);
						// 3.当前商品的开始结束时间
						to.setStartTime(session.getStartTime().getTime());
						to.setEndTime(session.getEndTime().getTime());

						// 4.随机码
						String token = UUID.randomUUID().toString().replaceAll("-", "");
						to.setRandomCode(token);

						// 5.使用库存作为分布式信号量 限流
						RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHOR + token);
						semaphore.trySetPermits(sku.getSeckillCount().intValue());

						String s = JSON.toJSONString(to);
						ops.put(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString(), s);
					}

				});
			});
		}

	}
}
