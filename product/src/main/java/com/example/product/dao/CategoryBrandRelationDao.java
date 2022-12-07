package com.example.product.dao;

import com.example.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

	void updateCategory(@Param("catId") Long catId, @Param("name") String name);
}
