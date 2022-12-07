package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-09-13 4:50 PM
 */
@Data
public class SkuReductionTo {
	private Long skuId;
	private int fullCount;
	private BigDecimal discount;
	private int countStatus;
	private BigDecimal fullPrice;
	private BigDecimal reducePrice;
	private int priceStatus;
	private List<MemberPrice> memberPrice;
}
