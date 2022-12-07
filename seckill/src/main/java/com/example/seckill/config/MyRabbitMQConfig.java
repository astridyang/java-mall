package com.example.seckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author sally
 * @date 2022-10-13 17:17
 */
@Configuration
public class MyRabbitMQConfig {


	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}


}