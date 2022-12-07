package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.constant.ProductConstant;
import com.example.product.dao.AttrAttrgroupRelationDao;
import com.example.product.dao.AttrGroupDao;
import com.example.product.dao.CategoryDao;
import com.example.product.entity.*;
import com.example.product.service.CategoryService;
import com.example.product.vo.AttrRelationVo;
import com.example.product.vo.AttrRespVo;
import com.example.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.AttrDao;
import com.example.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Resource
	AttrAttrgroupRelationDao relationDao;

	@Resource
	CategoryDao categoryDao;

	@Resource
	AttrGroupDao attrGroupDao;

	@Resource
	CategoryService categoryService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				new QueryWrapper<AttrEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		// 保存基本数据
		this.save(attrEntity);
		// 保存关联关系
		if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
			AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
			relation.setAttrGroupId(attr.getAttrGroupId());
			relation.setAttrId(attrEntity.getAttrId());
			relationDao.insert(relation);
		}
	}

	/**
	 * 基本属性和销售属性公用方法
	 *
	 * @param params
	 * @param catelogId
	 * @param attrType
	 * @return
	 */
	@Override
	public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
		String key = (String) params.get("key");
		if (catelogId != 0) {
			wrapper.eq("catelog_id", catelogId);
		}

		if (!StringUtils.isEmpty(key)) {
			wrapper.and((obj) -> {
				obj.eq("attr_id", key).or().like("attr_name", key);
			});
		}

		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				wrapper
		);
		PageUtils pageUtils = new PageUtils(page);
		List<AttrEntity> records = page.getRecords();
		List<AttrRespVo> respVoList = records.stream().map((attrEntity) -> {
			AttrRespVo attrRespVo = new AttrRespVo();
			BeanUtils.copyProperties(attrEntity, attrRespVo);
			// 设置分类和分组名字
			CategoryEntity category = categoryDao.selectById(attrRespVo.getCatelogId());
			if (category != null) {
				attrRespVo.setCatelogName(category.getName());
			}
			if ("base".equalsIgnoreCase(attrType)) {
				AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
				if (attrId != null && attrId.getAttrGroupId() != null) {
					AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId);
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}
			return attrRespVo;
		}).collect(Collectors.toList());
		pageUtils.setList(respVoList);
		return pageUtils;
	}

	@Override
	public AttrRespVo getAttrInfo(Long attrId) {
		AttrRespVo attrRespVo = new AttrRespVo();
		AttrEntity attrEntity = this.getById(attrId);
		BeanUtils.copyProperties(attrEntity, attrRespVo);
		// todo 分组名称和分类名称？
		// 分组id
		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
			if (relationEntity != null) {
				attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
			}
		}
		//分类完整路径
		Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
		attrRespVo.setCatelogPath(catelogPath);
		return attrRespVo;
	}

	@Transactional
	@Override
	public void updateAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		this.updateById(attrEntity);

		// 修改或新增分组关联
		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attr.getAttrGroupId());
			relationEntity.setAttrId(attr.getAttrId());
			if (count > 0) {
				// 修改
				relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			} else {
				// 新增
				relationDao.insert(relationEntity);
			}
		}

	}

	/**
	 * 根据分组id查找关联的所有基本属性
	 *
	 * @param attrgroupId
	 * @return
	 */
	@Override
	public List<AttrEntity> getRelationAttr(Long attrgroupId) {
		List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
		List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
		if (attrIds.size() == 0) {
			return null;
		}
		Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
		return (List<AttrEntity>) attrEntities;
	}

	@Override
	public void deleteRelation(AttrRelationVo[] vos) {
		List<AttrAttrgroupRelationEntity> entities = Arrays.stream(vos).map((item) -> {
			AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
			return attrAttrgroupRelationEntity;
		}).collect(Collectors.toList());
		relationDao.deleteBatchRelation(entities);
	}

	@Override
	public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
		// 1.当前分组只能关联所属分类下的属性
		AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
		Long catelogId = attrGroupEntity.getCatelogId();
		// 2.只能关联没有引用的属性
		// 2.1 当前分类下的所有分组
		List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		List<Long> groupIds = groupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
		// 2.2 这些分组关联的属性
		List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
		List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
		// 2.3 从当前分类的所有属性中移除这些属性
		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
		if (attrIds.size() > 0) {
			wrapper.notIn("attr_id", attrIds);
		}
		// key
		String key = (String) params.get("key");
		if (StringUtils.hasLength(key)) {
			wrapper.and(w -> {
				w.eq("attr_id", key).or().like("attr_name", key);
			});
		}
		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
		return new PageUtils(page);
	}

	@Override
	public List<Long> selectSearchAttrs(List<Long> attrIds) {

		return baseMapper.selectSearchAttrs(attrIds);
	}

}