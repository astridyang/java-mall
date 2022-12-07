package com.example.product.vo;

import lombok.Data;

/**
 * @author sally
 * @date 2022-08-22 2:59 PM
 */
@Data
public class AttrRespVo extends AttrVo {
	/**
	 * 所属分类名字
	 */
	private String catelogName;
	/**
	 * 所属分组名字
	 */
	private String groupName;

	/**
	 * 分类完整路径
	 */
	private Long[] catelogPath;
}
