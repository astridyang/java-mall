package com.example.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-10-18 17:25
 */
@Data
public class WareSkuLockVo {
	private String orderSn;

	private List<OrderItemVo> locks;//需要锁住的所有库存信息
}
