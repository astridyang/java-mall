package com.example.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @author sally
 * @date 2022-10-08 17:32
 */
@ToString
@Data
public class SpuSaleAttrVo {
	private Long attrId;
	private String attrName;
	private List<SpuSaleAttrWithSkuId> attrValues;
}
