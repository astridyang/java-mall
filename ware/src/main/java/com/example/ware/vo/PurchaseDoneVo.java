package com.example.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-15 9:22
 */
@Data
public class PurchaseDoneVo {
	private Long id;
	private List<PurchaseDoneItem> items;
}
