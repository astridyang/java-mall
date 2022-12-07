package com.example.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author sally
 * @date 2022-09-22 14:28
 */
@Configuration
public class MyRedissonConfig {
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() throws IOException {
		Config config = new Config();
		// 单节点
		config.useSingleServer().setAddress("redis://192.168.62.31:6379");
		return Redisson.create(config);
	}

}
