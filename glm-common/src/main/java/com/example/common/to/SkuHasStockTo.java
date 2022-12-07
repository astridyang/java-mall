package com.example.common.to;

import lombok.Data;

/**
 * @author sally
 * @date 2022-09-20 9:37
 */
@Data
public class SkuHasStockTo {
	private Long skuId;
	private Boolean hasStock;
}
