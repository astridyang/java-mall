package com.example.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-23 14:51
 */
@Data
public class SearchParam {
	private String keyword; // 全文匹配关键字
	private Long catalog3Id; // 三级分类id

	/**
	 * sort=saleCount_asc/desc
	 * sort=price_asc/desc 字符串skuPrice和下面价格区间冲突
	 * sort=hotScore_asc/desc 综合
	 */
	private String sort;

	/**
	 * 过滤条件
	 * hasStock=0/1
	 * skuPrice=1_500/_500/500_
	 * brandId=1
	 * attrs=1_5寸:6寸
	 */
	private Integer hasStock;
	private String skuPrice;
	private List<Long> brandId;
	private List<String> attrs;

	private Integer pageNum=1; // 页码
}
