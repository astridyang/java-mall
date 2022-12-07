package com.example.ware.vo;

import lombok.Data;

/**
 * @author sally
 * @date 2022-09-15 9:22
 */
@Data
public class PurchaseDoneItem {
	//itemId:1,status:4,reason:""
	private Long itemId;
	private Integer status;
	private String reason;
}
