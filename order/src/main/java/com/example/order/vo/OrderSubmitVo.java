package com.example.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-10-17 17:17
 */
@Data
public class OrderSubmitVo {
    private Long addrId;
    private String payType;
    private BigDecimal payPrice;
    private String orderToken;
}
