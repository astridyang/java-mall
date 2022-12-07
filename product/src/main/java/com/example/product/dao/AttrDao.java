package com.example.product.dao;

import com.example.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

	List<Long> selectSearchAttrs(@Param("attrIds") List<Long> attrIds);
}
