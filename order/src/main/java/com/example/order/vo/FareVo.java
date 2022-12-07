package com.example.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-10-18 15:28
 */
@Data
public class FareVo {
    private MemberAddressVo address;
	private BigDecimal fare;
}
