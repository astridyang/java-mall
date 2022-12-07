package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.AttrGroupEntity;
import com.example.product.vo.AttrGroupWithAttrVo;
import com.example.product.vo.AttrRelationVo;
import com.example.product.vo.SpuAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPage(Map<String, Object> params, Long cateId);

	List<AttrGroupWithAttrVo> getGroupWithAttrsByCateId(Long catelogId);

	List<SpuAttrGroupVo> getGroupWithAttrs(Long spuId, Long catalogId);
}

