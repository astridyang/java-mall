package com.example.order.config;

import com.example.common.constant.OrderMq;
import com.example.common.constant.WareMq;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sally
 * @date 2022-10-20 16:27
 */
@Configuration
public class MyMQConfig {
	@Bean
	public Queue OrderDelayQueue() {
		// String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", OrderMq.ORDER_EVENT_EXCHANGE);
		arguments.put("x-dead-letter-routing-key", OrderMq.ORDER_RELEASE_KEY);
		arguments.put("x-message-ttl", 60000);
		return new Queue(OrderMq.ORDER_DELAY_QUEUE, true, false, false, arguments);
	}

	@Bean
	public Queue orderReleaseOrderQueue() {
		return new Queue(OrderMq.ORDER_RELEASE_QUEUE, true, false, false);
	}

	@Bean
	public Queue orderSeckillOrderQueue() {
		return new Queue(OrderMq.ORDER_SECKILL_QUEUE, true, false, false);
	}

	@Bean
	public Exchange orderEventExchange() {
		//String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
		return new TopicExchange(OrderMq.ORDER_EVENT_EXCHANGE, true, false);
	}

	@Bean
	public Binding OrderCreateOrderBinding() {
		return new Binding(OrderMq.ORDER_DELAY_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderMq.ORDER_EVENT_EXCHANGE,
				OrderMq.ORDER_CREATE_KEY,
				null);
	}

	@Bean
	Binding OrderReleaseOrderBinding() {
		return new Binding(OrderMq.ORDER_RELEASE_QUEUE, Binding.DestinationType.QUEUE, OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_RELEASE_KEY, null);
	}

	@Bean
	Binding OrderReleaseOtherBinding() {
		return new Binding(WareMq.STOCK_RELEASE_QUEUE, Binding.DestinationType.QUEUE, OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_RELEASE_OTHER_KEY, null);
	}

	@Bean
	Binding OrderSeckillOrderBinding() {
		return new Binding(OrderMq.ORDER_SECKILL_QUEUE, Binding.DestinationType.QUEUE, OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_SECKILL_KEY, null);
	}

}
