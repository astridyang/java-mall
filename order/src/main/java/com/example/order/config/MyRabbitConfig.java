package com.example.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-10-13 17:17
 */
@Configuration
public class MyRabbitConfig {
	@Resource
	RabbitTemplate rabbitTemplate;

	@PostConstruct
	public void initRabbitTemplate() {
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {


			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				/**
				 * 1.消息确认（publisher，consumer（手动ack）两端确认）
				 * 2.每一个消息都记录在数据库，定期将失败消息再发送
				 */
				// 服务器收到了
				System.out.println("confirm..." + correlationData + "; ack==>" + ack + "; cause==>" + cause);
			}
		});
		rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback(){
			/**
			 * 消息没有投递给指定队列，触发失败回调
			 * @param returned
			 */
			@Override
			public void returnedMessage(ReturnedMessage returned) {
				// 修改数据库当前消息的错误状态
				System.out.println(returned);
			}
		});
	}
}
