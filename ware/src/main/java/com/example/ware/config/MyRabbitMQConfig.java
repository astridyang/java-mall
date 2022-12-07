package com.example.ware.config;

import com.example.common.constant.WareMq;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sally
 * @date 2022-10-20 17:13
 */
@Configuration
public class MyRabbitMQConfig {
	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Exchange stockEventExchange() {
		return new TopicExchange(WareMq.STOCK_EVENT_EXCHANGE, true, false);
	}

	@Bean
	public Queue stockReleaseStockQueue() {
		return new Queue(WareMq.STOCK_RELEASE_QUEUE, true, false, false);
	}

	@Bean
	public Queue stockDelayQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", WareMq.STOCK_EVENT_EXCHANGE);
		arguments.put("x-dead-letter-routing-key", WareMq.STOCK_RELEASE_KEY);
		arguments.put("x-message-ttl", 120000);
		return new Queue(WareMq.STOCK_DELAY_QUEUE, true, false, false, arguments);
	}

	@Bean
	public Binding stockReleaseBinding() {
		return new Binding(WareMq.STOCK_RELEASE_QUEUE, Binding.DestinationType.QUEUE, WareMq.STOCK_EVENT_EXCHANGE, WareMq.STOCK_RELEASE_KEY, null);
	}

	@Bean
	public Binding stockLockedBinding() {
		return new Binding(WareMq.STOCK_DELAY_QUEUE, Binding.DestinationType.QUEUE, WareMq.STOCK_EVENT_EXCHANGE, WareMq.STOCK_LOCKED_KEY, null);
	}
}
