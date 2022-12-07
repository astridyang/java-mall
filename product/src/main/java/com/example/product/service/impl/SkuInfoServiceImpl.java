package com.example.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.product.entity.SkuImagesEntity;
import com.example.product.entity.SpuInfoDescEntity;
import com.example.product.feign.SeckillFeignService;
import com.example.product.service.*;
import com.example.product.vo.SkuItemVo;
import com.example.product.vo.SkuSeckillVo;
import com.example.product.vo.SpuAttrGroupVo;
import com.example.product.vo.SpuSaleAttrVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.SkuInfoDao;
import com.example.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Resource
	SkuImagesService skuImagesService;

	@Resource
	SpuInfoDescService spuInfoDescService;

	@Resource
	AttrGroupService attrGroupService;

	@Resource
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Resource
	SeckillFeignService seckillFeignService;

	@Resource
	ThreadPoolExecutor executor;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				new QueryWrapper<SkuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
		this.baseMapper.insert(skuInfoEntity);
	}

	@Override
	public PageUtils queryByCondition(Map<String, Object> params) {
		// key:
		// catelogId: 0
		// brandId: 0
		// min: 0
		// max: 0
		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

		String key = (String) params.get("key");
		if (StringUtils.hasLength(key)) {
			wrapper.and(w -> {
				w.eq("sku_id", key).or().like("sku_name", key);
			});
		}
		String catelogId = (String) params.get("catelogId");
		if (StringUtils.hasLength(catelogId)) {
			if (!"0".equals(catelogId)) {
				wrapper.eq("catalog_id", catelogId);
			}
		}
		String brandId = (String) params.get("brandId");
		if (StringUtils.hasLength(brandId)) {
			if (!"0".equals(brandId)) {
				wrapper.eq("brand_id", brandId);
			}
		}
		String min = (String) params.get("min");
		if (StringUtils.hasLength(min)) {
			wrapper.ge("price", min);
		}
		String max = (String) params.get("max");
		if (StringUtils.hasLength(max)) {
			try {
				BigDecimal decimal = new BigDecimal(max);
				if (decimal.compareTo(new BigDecimal("0")) > 0) {
					wrapper.le("price", max);
				}
			} catch (Exception ignored) {

			}

		}

		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
		return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
	}

	@Override
	public SkuItemVo item(Long skuId) {
		SkuItemVo vo = new SkuItemVo();

		CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
			// 1.sku基本信息获取 pms_sku_info
			SkuInfoEntity info = getById(skuId);
			vo.setSkuInfo(info);
			return info;
		}, executor);

		CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
			// 3.获取spu的销售属性组合
			List<SpuSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
			vo.setSaleAttrs(saleAttrVos);
		}, executor);

		CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
			// 4.获取spu的介绍 pms_spu_info_desc
			SpuInfoDescEntity desc = spuInfoDescService.getById(res.getSpuId());
			vo.setDesc(desc);
		}, executor);

		CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync(res -> {
			// 5.获取spu的规格参数信息
			List<SpuAttrGroupVo> attrGroupVos = attrGroupService.getGroupWithAttrs(res.getSpuId(), res.getCatalogId());
			vo.setGroupAttrs(attrGroupVos);
		}, executor);

		CompletableFuture<Void> imgFuture = CompletableFuture.runAsync(() -> {
			// 2.sku的图片信息 pms_sku_images
			List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
			vo.setImages(images);
		}, executor);

		// 获取sku 秒杀信息
		CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
			R r = seckillFeignService.getSkuSeckillInfo(skuId);
			if(r.getCode() == 0){
				vo.setSkuSeckillInfo(r.getData(new TypeReference<SkuSeckillVo>(){}));
			}
		}, executor);

		try {
			CompletableFuture.allOf(saleAttrFuture,descFuture,groupAttrFuture,imgFuture,seckillFuture).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}


		return vo;
	}

}