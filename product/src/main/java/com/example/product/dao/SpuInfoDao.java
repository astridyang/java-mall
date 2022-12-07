package com.example.product.dao;

import com.example.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:18
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

	void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
