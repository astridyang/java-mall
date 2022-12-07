package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void updateDetail(BrandEntity brand);
}

