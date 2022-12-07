package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:18
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

