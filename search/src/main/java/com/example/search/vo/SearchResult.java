package com.example.search.vo;

import com.example.common.to.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-23 15:30
 */
@Data
public class SearchResult {
	private List<SkuEsModel> products;

	/**
	 * 分页信息
	 */
	private Integer pageNum; // 当前页码
	private Long total;// 总记录数
	private Integer totalPages;// 总页码
	private List<Integer> pageNavigation;

	private List<BrandVo> brands; // 搜索结果涉及的所有品牌
	private List<CatalogVo> catalogs; // 涉及的所有分类
	private List<AttrVo> attrs; // 涉及的所有属性

	@Data
	public static class BrandVo {
		private Long brandId;
		private String brandName;
		private String brandImg;
	}

	@Data
	public static class CatalogVo {
		private Long catalogId;
		private String catalogName;
	}

	@Data
	public static class AttrVo {
		private Long attrId;
		private String attrName;
		private List<String> attrValue;
	}

}
