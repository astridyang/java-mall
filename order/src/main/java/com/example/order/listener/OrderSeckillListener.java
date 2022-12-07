package com.example.order.listener;

import com.example.common.constant.OrderMq;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author sally
 * @date 2022-11-10 13:50
 */
@RabbitListener(queues = OrderMq.ORDER_SECKILL_QUEUE)
@Component
public class OrderSeckillListener {
	@Resource
	OrderService orderService;

	@RabbitHandler
	public void listener(SeckillOrderTo order, Channel channel, Message message) throws IOException {
		System.out.println("preparing to create seckill order..." + order.getOrderSn());
		try {
			orderService.seckillOrder(order);
			// 可以调用支付宝自动收单
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
}
