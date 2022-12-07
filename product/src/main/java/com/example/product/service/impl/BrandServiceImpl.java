package com.example.product.service.impl;

import com.example.product.service.CategoryBrandRelationService;
import com.example.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.BrandDao;
import com.example.product.entity.BrandEntity;
import com.example.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

	@Lazy
	@Resource
	CategoryBrandRelationService categoryBrandRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		// 获取 key
		// 模糊查询

		String key = (String) params.get("key");
		QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.eq("brand_id", key).or().like("name", key);
		}

		IPage<BrandEntity> page = this.page(
				new Query<BrandEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void updateDetail(BrandEntity brand) {
		// 保证冗余字段数据一致
		this.updateById(brand);
		if (!StringUtils.isEmpty(brand.getName())) {
			// 同步更新其他关联表的数据
			categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
			// todo 更新其他关联
		}
	}

}