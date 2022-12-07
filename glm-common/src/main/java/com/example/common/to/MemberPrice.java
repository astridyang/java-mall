package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-09-13 11:43 AM
 */
@Data
public class MemberPrice {
    private Long id;
	private String name;
	private BigDecimal price;
}
