package com.example.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sally
 * @date 2022-10-09 15:01
 */
@Configuration
public class MyThreadConfig {
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool) {
		return new ThreadPoolExecutor(pool.getCorePoolSize(), pool.getMaxPoolSize(), pool.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
	}
}
