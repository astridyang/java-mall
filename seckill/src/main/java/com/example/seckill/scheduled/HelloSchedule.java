package com.example.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author sally
 * @date 2022-11-08 14:22
 */
@EnableScheduling
@EnableAsync
@Slf4j
// @Component
public class HelloSchedule {

	@Async
	@Scheduled(cron = "* * * ? * 2")
	public void hello() throws InterruptedException {
		log.info("hello....");
		Thread.sleep(3000);
	}
}
