package com.example.order.controller;

import com.example.order.entity.OrderReturnReasonEntity;
import com.example.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @author sally
 * @date 2022-10-13 16:26
 */
@RestController
public class RabbitController {

	@Resource
	RabbitTemplate rabbitTemplate;

	@GetMapping("/sendMsg")
	public String sendMsg(@RequestParam(value = "num", defaultValue = "10") Integer num) {
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				OrderReturnReasonEntity reason = new OrderReturnReasonEntity();
				reason.setId(1L);
				reason.setCreateTime(new Date());
				reason.setName("reason " + i);
				rabbitTemplate.convertAndSend("java-exchange", "java", reason,new CorrelationData(UUID.randomUUID().toString()));
			} else {
				OrderEntity order = new OrderEntity();
				order.setId(1L);
				order.setOrderSn("order " + i);
				rabbitTemplate.convertAndSend("java-exchange", "java22", order);
			}
		}
		return "ok";
	}


}
