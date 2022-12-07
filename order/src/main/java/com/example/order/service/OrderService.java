package com.example.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.common.utils.PageUtils;
import com.example.order.entity.OrderEntity;
import com.example.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:22:12
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

	OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

	SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

	OrderEntity getOrderByOrderSn(String orderSn);

	void closeOrder(OrderTo orderEntity);

	PayVo getPayOrder(String orderSn);

	PageUtils queryPageWithItem(Map<String, Object> params);

	String handleAlipayResult(PayAsyncVo vo);

	void seckillOrder(SeckillOrderTo order);
}

