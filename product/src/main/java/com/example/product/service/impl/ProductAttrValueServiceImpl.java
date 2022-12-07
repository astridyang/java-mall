package com.example.product.service.impl;

import com.example.product.entity.AttrEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.ProductAttrValueDao;
import com.example.product.entity.ProductAttrValueEntity;
import com.example.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<ProductAttrValueEntity> page = this.page(
				new Query<ProductAttrValueEntity>().getPage(params),
				new QueryWrapper<ProductAttrValueEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveProductAttr(List<ProductAttrValueEntity> collect) {
		this.saveBatch(collect);
	}

	@Override
	public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {

		return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

	}

	@Override
	public void updateAttr(Long spuId, List<ProductAttrValueEntity> entityList) {
		// 1. 删除spu原来的所有属性
		this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
		// 2. 新增attr
		List<ProductAttrValueEntity> collect = entityList.stream().peek(item -> {
			item.setSpuId(spuId);
		}).collect(Collectors.toList());
		this.saveBatch(collect);
	}

}