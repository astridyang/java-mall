package com.example.order.to;

import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItemEntity;
import com.example.order.vo.OrderItemVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-10-18 14:52
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
	private List<OrderItemEntity> items;

	private BigDecimal payPrice; // 订单应付价格
	private BigDecimal fare; // 运费
}
