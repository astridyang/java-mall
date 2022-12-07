package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.SpuInfoEntity;
import com.example.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:18
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveSpuInfo(SpuSaveVo vo);

	void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

	PageUtils queryByCondition(Map<String, Object> params);

	void up(Long spuId);

	SpuInfoEntity getSpuBySkuId(Long id);
}

