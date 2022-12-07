package com.example.product.service.impl;

import com.example.product.dao.BrandDao;
import com.example.product.dao.CategoryDao;
import com.example.product.entity.BrandEntity;
import com.example.product.entity.CategoryEntity;
import com.example.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.CategoryBrandRelationDao;
import com.example.product.entity.CategoryBrandRelationEntity;
import com.example.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

	@Resource
	BrandDao brandDao;

	@Resource
	CategoryDao categoryDao;

	@Resource
	CategoryBrandRelationDao relationDao;

	@Lazy
	@Resource
	BrandService brandService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryBrandRelationEntity> page = this.page(
				new Query<CategoryBrandRelationEntity>().getPage(params),
				new QueryWrapper<CategoryBrandRelationEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
		Long brandId = categoryBrandRelation.getBrandId();
		Long catelogId = categoryBrandRelation.getCatelogId();
		// brand name
		BrandEntity brandEntity = brandDao.selectById(brandId);
		// category name
		CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
		categoryBrandRelation.setBrandName(brandEntity.getName());
		categoryBrandRelation.setCatelogName(categoryEntity.getName());
		this.save(categoryBrandRelation);
	}

	@Override
	public void updateBrand(Long brandId, String name) {
		CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
		entity.setBrandId(brandId);
		entity.setBrandName(name);
		this.update(entity, new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
	}

	@Override
	public void updateCategory(Long catId, String name) {
		this.baseMapper.updateCategory(catId, name);
	}

	@Override
	public List<BrandEntity> getBrandListByCatId(Long catId) {
		List<CategoryBrandRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
		List<BrandEntity> brandEntities = relationEntities.stream().map(item -> {
			Long brandId = item.getBrandId();
			return brandService.getById(brandId);
		}).collect(Collectors.toList());

		return brandEntities;

	}


}