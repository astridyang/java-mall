package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.AttrAttrgroupRelationEntity;
import com.example.product.vo.AttrRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveRelation(List<AttrRelationVo> vos);
}

