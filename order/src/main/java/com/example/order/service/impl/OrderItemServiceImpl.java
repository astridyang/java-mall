package com.example.order.service.impl;

import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.order.dao.OrderItemDao;
import com.example.order.entity.OrderItemEntity;
import com.example.order.service.OrderItemService;


@Slf4j
// @RabbitListener(queues = {"java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OrderItemEntity> page = this.page(
				new Query<OrderItemEntity>().getPage(params),
				new QueryWrapper<OrderItemEntity>()
		);

		return new PageUtils(page);
	}

	// @RabbitHandler
	public void receiveMsg(Message message, OrderReturnReasonEntity content, Channel channel) {
		System.out.println("content: " + content.getName());
		// channel内按顺序自增
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {
			if (deliveryTag % 2 == 0) {
				channel.basicAck(deliveryTag, false);
				System.out.println("手动确认接受：" + deliveryTag);
			}else{
				channel.basicNack(deliveryTag,false,false);
				System.out.println("手动拒绝接受: " + deliveryTag);
			}
		} catch (Exception e) {

		}
	}

	// @RabbitHandler
	public void receiveMsg(Message message, OrderEntity content, Channel channel) {
		System.out.println("content: " + content.getOrderSn());
	}


}