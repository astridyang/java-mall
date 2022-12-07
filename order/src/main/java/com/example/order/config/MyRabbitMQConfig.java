package com.example.order.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-10-13 15:26
 */
@Configuration
public class MyRabbitMQConfig {


	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}


}
