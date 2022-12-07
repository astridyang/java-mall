package com.example.product.dao;

import com.example.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
