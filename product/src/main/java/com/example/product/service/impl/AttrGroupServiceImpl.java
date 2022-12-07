package com.example.product.service.impl;

import com.example.product.entity.AttrAttrgroupRelationEntity;
import com.example.product.entity.AttrEntity;
import com.example.product.service.AttrService;
import com.example.product.vo.AttrGroupWithAttrVo;
import com.example.product.vo.AttrRelationVo;
import com.example.product.vo.SpuAttrGroupVo;
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

import com.example.product.dao.AttrGroupDao;
import com.example.product.entity.AttrGroupEntity;
import com.example.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Resource
	AttrService attrService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long cateId) {
		String key = (String) params.get("key");
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((obj) -> {
				obj.eq("attr_group_id", key).or().like("attr_group_name", key);
			});
		}
		if (cateId == 0) {
			IPage<AttrGroupEntity> page = this.page(
					new Query<AttrGroupEntity>().getPage(params),
					wrapper
			);
			return new PageUtils(page);
		} else {
			wrapper.eq("catelog_id", cateId);
			IPage<AttrGroupEntity> page = this.page(
					new Query<AttrGroupEntity>().getPage(params),
					wrapper
			);
			return new PageUtils(page);
		}
	}

	@Override
	public List<AttrGroupWithAttrVo> getGroupWithAttrsByCateId(Long catelogId) {
		// 1.查询分组
		List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		// 2.查询所有属性
		List<AttrGroupWithAttrVo> collect = groupEntities.stream().map(group -> {
			AttrGroupWithAttrVo vo = new AttrGroupWithAttrVo();
			BeanUtils.copyProperties(group, vo);
			List<AttrEntity> attrs = attrService.getRelationAttr(group.getAttrGroupId());
			vo.setAttrs(attrs);
			return vo;

		}).collect(Collectors.toList());

		return collect;
	}

	@Override
	public List<SpuAttrGroupVo> getGroupWithAttrs(Long spuId, Long catalogId) {

		AttrGroupDao groupDao = this.getBaseMapper();
		return groupDao.getGroupWithAttrs(spuId,catalogId);
	}


}