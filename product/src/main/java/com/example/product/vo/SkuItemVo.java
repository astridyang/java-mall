package com.example.product.vo;

import com.example.product.entity.SkuImagesEntity;
import com.example.product.entity.SkuInfoEntity;
import com.example.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author sally
 * @date 2022-10-08 16:07
 */
@Data
public class SkuItemVo {
	// 1.sku基本信息获取 pms_sku_info
	SkuInfoEntity skuInfo;
	boolean hasStock = true;
	// 2.sku的图片信息 pms_sku_images
	List<SkuImagesEntity> images;
	// 3.获取spu的销售属性组合
	List<SpuSaleAttrVo> saleAttrs;
	// 4.获取spu的介绍 pms_spu_info_desc
	SpuInfoDescEntity desc;
	// 5.获取spu的规格参数信息
	List<SpuAttrGroupVo> groupAttrs;

	SkuSeckillVo skuSeckillInfo;


}
