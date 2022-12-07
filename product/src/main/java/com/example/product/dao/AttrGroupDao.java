package com.example.product.dao;

import com.example.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.product.vo.SpuAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

	List<SpuAttrGroupVo> getGroupWithAttrs(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
