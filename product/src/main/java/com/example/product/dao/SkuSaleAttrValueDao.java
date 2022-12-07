package com.example.product.dao;

import com.example.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.product.vo.SpuSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

	List<SpuSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);

	List<String> getAttrString(@Param("skuId") Long skuId);
}
