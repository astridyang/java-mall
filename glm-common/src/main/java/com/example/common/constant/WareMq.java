package com.example.common.constant;

/**
 * @author sally
 * @date 2022-10-21 15:49
 */
public class WareMq {
	public static String STOCK_EVENT_EXCHANGE = "stock-event-exchange";
	public static String STOCK_LOCKED_KEY = "stock.locked";
	public static String STOCK_RELEASE_KEY = "stock.release.#";
	public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
	public static final String STOCK_RELEASE_QUEUE = "stock.release.stock.queue";
}
