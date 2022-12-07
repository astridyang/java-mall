package com.example.ware.listener;

import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.constant.WareMq;
import com.example.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author sally
 * @date 2022-10-21 17:58
 */
@RabbitListener(queues = WareMq.STOCK_RELEASE_QUEUE)
@Service
public class StockReleaseListener {
	@Resource
	WareSkuService wareSkuService;
	/**
	 * 启动手动应答模式，只要解锁库存失败，重试解锁
	 * 消息拒绝后重新放回队列
	 *
	 * @param to
	 * @param message
	 */
	@RabbitHandler
	public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
		System.out.println("receive release stock message.");
		long tag = message.getMessageProperties().getDeliveryTag();
		try {
			wareSkuService.unLockStock(to);
			channel.basicAck(tag, false);
		}catch (Exception e){
			channel.basicReject(tag, true);
		}
	}

	@RabbitHandler
	public void handleOrderCloseRelease(OrderTo order, Message message, Channel channel) throws IOException {
		System.out.println("receive order cancel to release stock message.");
		long tag = message.getMessageProperties().getDeliveryTag();
		try {
			wareSkuService.unLockStock(order);
			channel.basicAck(tag, false);
		}catch (Exception e){
			channel.basicReject(tag, true);
		}
	}
}
