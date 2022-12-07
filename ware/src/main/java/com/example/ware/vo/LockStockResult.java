package com.example.ware.vo;

import lombok.Data;

/**
 * @author sally
 * @date 2022-10-18 17:46
 */
@Data
public class LockStockResult {
	private Long skuId;
	private Integer num; // 锁定数量
	private Boolean locked; // 是否成功锁定
}
