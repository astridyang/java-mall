package com.example.common.constant;

/**
 * @author sally
 * @date 2022-11-01 15:41
 */
public class OrderMq {
	public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
	public static final String ORDER_CREATE_KEY = "order.create.order";
	public static final String ORDER_RELEASE_KEY = "order.release.order";
	public static final String ORDER_RELEASE_OTHER_KEY = "order.release.other.#";
	public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
	public static final String ORDER_RELEASE_QUEUE = "order.release.order.queue";

	public static final String ORDER_SECKILL_QUEUE = "order.seckill.order.queue";
	public static final String ORDER_SECKILL_KEY = "order.seckill.order";
}
