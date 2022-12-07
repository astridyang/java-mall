package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:18
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveProductAttr(List<ProductAttrValueEntity> collect);

	List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

	void updateAttr(Long spuId, List<ProductAttrValueEntity> entityList);
}

