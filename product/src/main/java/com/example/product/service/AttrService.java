package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.AttrEntity;
import com.example.product.entity.ProductAttrValueEntity;
import com.example.product.vo.AttrRelationVo;
import com.example.product.vo.AttrRespVo;
import com.example.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveAttr(AttrVo attr);

	PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

	AttrRespVo getAttrInfo(Long attrId);

	void updateAttr(AttrVo attr);

	List<AttrEntity> getRelationAttr(Long attrgroupId);

	void deleteRelation(AttrRelationVo[] vos);

	PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

	List<Long> selectSearchAttrs(List<Long> attrIds);
}

