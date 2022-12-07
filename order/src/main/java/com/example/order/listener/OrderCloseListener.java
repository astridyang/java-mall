package com.example.order.listener;

import com.example.common.constant.OrderMq;
import com.example.common.to.mq.OrderTo;
import com.example.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author sally
 * @date 2022-11-01 14:55
 */
@Service
@RabbitListener(queues = OrderMq.ORDER_RELEASE_QUEUE)
public class OrderCloseListener {
	@Resource
	OrderService orderService;

	@RabbitHandler
	public void listener(OrderTo order, Channel channel, Message message) throws IOException {
		System.out.println("receive expired order message, preparing to close order..." + order.getOrderSn());
		try {
			orderService.closeOrder(order);
			// 可以调用支付宝自动收单
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
}
