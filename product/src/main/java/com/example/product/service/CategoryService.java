package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.CategoryEntity;
import com.example.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

	Long[] findCatelogPath(Long catelogId);

	void updateCascade(CategoryEntity category);

	List<CategoryEntity> getLevel1Categories();

	Map<String, List<Catelog2Vo>> getCatelogJson();
}

