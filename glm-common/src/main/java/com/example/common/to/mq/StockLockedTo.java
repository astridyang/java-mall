package com.example.common.to.mq;

import lombok.Data;

/**
 * @author sally
 * @date 2022-10-21 15:47
 */
@Data
public class StockLockedTo {
	private Long id;
	private StockDetailTo detail;
}
