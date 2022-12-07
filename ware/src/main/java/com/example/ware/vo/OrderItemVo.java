package com.example.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-10-14 15:02
 */
@Data
public class OrderItemVo {
	private Long skuId;
	private boolean check = true;
	private String title;
	private String image;
	private List<String> skuAttr;
	private BigDecimal price;
	private Integer count;
	private BigDecimal totalPrice;

}
