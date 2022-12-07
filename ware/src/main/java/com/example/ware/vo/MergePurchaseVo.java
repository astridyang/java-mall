package com.example.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-14 15:59
 */
@Data
public class MergePurchaseVo {
	private Long purchaseId;// : 1, //整单id
	private List<Long> items;// :[1,2,3,4] //合并项集合
}
