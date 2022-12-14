package com.example.product.service.impl;

import com.example.product.vo.AttrRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.AttrAttrgroupRelationDao;
import com.example.product.entity.AttrAttrgroupRelationEntity;
import com.example.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveRelation(List<AttrRelationVo> vos) {
		List<AttrAttrgroupRelationEntity> entities = vos.stream().map(item -> {
			AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(item, entity);
			return entity;
		}).collect(Collectors.toList());
		this.saveBatch(entities);
	}

}