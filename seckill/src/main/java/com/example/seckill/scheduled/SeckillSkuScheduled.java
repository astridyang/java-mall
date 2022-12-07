package com.example.seckill.scheduled;

import com.example.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 *
 * @author sally
 * @date 2022-11-08 14:54
 */
@Service
@Slf4j
public class SeckillSkuScheduled {

	@Resource
	SeckillService seckillService;

	@Resource
	RedissonClient redissonClient;
	private final String up_lock = "seckill:up:lock";

	/**
	 * （每晚3点，上架最近3天需要参加秒杀的商品） "0 0 3 * * ?"
	 * "0 * * * * ?" 每分钟
	 */
	@Scheduled(cron = "*/10 * * * * ?")
	public void upSeckillSkuLatest3Days() {
		log.info("上架商品定时任务...");
		// 加分布式锁
		RLock lock = redissonClient.getLock(up_lock);
		lock.lock(10, TimeUnit.SECONDS);
		try {
			seckillService.upSeckillSkuLatest3Days();
		} finally {
			lock.unlock();
		}
	}
}
