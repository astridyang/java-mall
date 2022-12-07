package com.example.product.vo;

import com.example.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-08-23 4:13 PM
 */
@Data
public class AttrGroupWithAttrVo {
	/**
	 * 分组id
	 */
	private Long attrGroupId;
	/**
	 * 组名
	 */
	private String attrGroupName;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 描述
	 */
	private String descript;
	/**
	 * 组图标
	 */
	private String icon;
	/**
	 * 所属分类id
	 */
	private Long catelogId;

	private List<AttrEntity> attrs;
}
