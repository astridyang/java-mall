package com.example.product.service.impl;

import com.example.common.constant.ProductConstant;
import com.example.common.to.SkuEsModel;
import com.example.common.to.SkuHasStockTo;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import com.example.product.entity.*;
import com.example.product.feign.CouponFeignService;
import com.example.product.feign.SearchFeignService;
import com.example.product.feign.WareFeignService;
import com.example.product.service.*;
import com.example.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import com.alibaba.fastjson.TypeReference;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Resource
	SpuInfoDescService spuInfoDescService;

	@Resource
	SpuImagesService spuImagesService;

	@Resource
	ProductAttrValueService productAttrValueService;

	@Resource
	SkuInfoService skuInfoService;

	@Resource
	SkuImagesService skuImagesService;

	@Resource
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Resource
	CouponFeignService couponFeignService;

	@Resource
	BrandService brandService;

	@Resource
	CategoryService categoryService;

	@Resource
	AttrService attrService;

	@Resource
	WareFeignService wareFeignService;

	@Resource
	SearchFeignService searchFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo vo) {
		// 1. 保存spu基本信息 pms_spu_info
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(vo, spuInfoEntity);
		spuInfoEntity.setCreateTime(new Date());
		spuInfoEntity.setUpdateTime(new Date());
		this.saveBaseSpuInfo(spuInfoEntity);

		// 2. 保存spu的描述图片 pms_spu_info_desc
		List<String> decript = vo.getDecript();
		SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
		descEntity.setSpuId(spuInfoEntity.getId());
		descEntity.setDecript(String.join(",", decript));
		spuInfoDescService.saveSpuInfoDesc(descEntity);

		// 3. 保存spu的图片集 pms_spu_images
		List<String> images = vo.getImages();
		spuImagesService.saveImages(spuInfoEntity.getId(), images);

		// 4. 保存spu的规格参数 pms_product_attr_value
		List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
		List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
			ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
			attrValueEntity.setAttrId(attr.getAttrId());
			attrValueEntity.setAttrName("");
			attrValueEntity.setAttrValue(attr.getAttrValues());
			attrValueEntity.setQuickShow(attr.getShowDesc());
			attrValueEntity.setSpuId(spuInfoEntity.getId());
			return attrValueEntity;
		}).collect(Collectors.toList());
		productAttrValueService.saveProductAttr(collect);

		// 5. 保存spu的积分信息 glm_sms->sms_spu_bounds
		Bounds bounds = vo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(spuInfoEntity.getId());
		R r = couponFeignService.saveSpuBounds(spuBoundTo);
		if (r.getCode() != 0) {
			log.error("远程保存sku积分信息失败");
		}


		// 6. 保存对应的所有sku信息
		List<Skus> skus = vo.getSkus();
		if (skus != null && skus.size() != 0) {
			skus.forEach(item -> {
				String defaultImg = "";
				for (Images image : item.getImages()) {
					if (image.getDefaultImg() == 1) {
						defaultImg = image.getImgUrl();
					}
				}

				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				BeanUtils.copyProperties(item, skuInfoEntity);
				skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
				skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoEntity.setSpuId(spuInfoEntity.getId());
				skuInfoEntity.setSkuDefaultImg(defaultImg);
				// 6.1) sku的基本信息 pms_sku_info
				skuInfoService.saveSkuInfo(skuInfoEntity);

				Long skuId = skuInfoEntity.getSkuId();

				// 6.2) sku的图片信息 pms_sku_images
				// 没有图片路径的无需保存
				List<SkuImagesEntity> collect1 = item.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
				}).filter(entity -> StringUtils.hasLength(entity.getImgUrl())).collect(Collectors.toList());

				skuImagesService.saveBatch(collect1);

				List<Attr> saleAttrs = item.getAttr();
				List<SkuSaleAttrValueEntity> saleAttrValueEntities = saleAttrs.stream().map(attr -> {
					SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(attr, entity);
					entity.setSkuId(skuId);
					return entity;
				}).collect(Collectors.toList());
				// 6.3) sku的销售属性信息 pms_sku_sale_attr_value
				skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

				// 6.4) sku的优惠、满减信息 glm_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(item, skuReductionTo);
				skuReductionTo.setSkuId(skuId);
				if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
					R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
					if (r1.getCode() != 0) {
						log.error("远程保存sku优惠信息失败");
					}
				}

			});
		}


	}

	@Override
	public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
		this.baseMapper.insert(spuInfoEntity);
	}

	@Override
	public PageUtils queryByCondition(Map<String, Object> params) {
		QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
		// status: 0
		// key:
		// brandId: 3
		// catelogId: 225
		String key = (String) params.get("key");
		if (StringUtils.hasLength(key)) {
			wrapper.and(w -> {
				w.eq("id", key).or().like("spu_name", key);
			});
		}
		String status = (String) params.get("status");
		if (StringUtils.hasLength(status)) {
			wrapper.eq("publish_status", status);
		}
		String brandId = (String) params.get("brandId");
		if (StringUtils.hasLength(brandId)) {
			if (!"0".equals(brandId)) {
				wrapper.eq("brand_id", brandId);
			}
		}
		String catelogId = (String) params.get("catelogId");
		if (StringUtils.hasLength(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);


	}

	@Override
	public void up(Long spuId) {
		// 查出所有sku 封装es
		List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
		List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
		// 查询sku所有可以被检索的规格属性
		List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
		List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
		List<Long> searchIds = attrService.selectSearchAttrs(attrIds);
		HashSet<Long> idSet = new HashSet<>(searchIds);
		// 过滤可以检索的属性
		List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> idSet.contains(item.getAttrId())).map(item -> {
			SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
			BeanUtils.copyProperties(item, attrs1);
			return attrs1;
		}).collect(Collectors.toList());

		// 远程调用库存系统查询是否有库存
		Map<Long, Boolean> stockMap = null;
		try {
			R result = wareFeignService.hasStock(skuIds);
			TypeReference<List<SkuHasStockTo>> typeReference = new TypeReference<List<SkuHasStockTo>>() {
			};
			stockMap = result.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
		} catch (Exception e) {
			log.error("远程查询库存失败，原因{}", e);
		}

		Map<Long, Boolean> finalStockMap = stockMap;
		List<SkuEsModel> collect = skus.stream().map(sku -> {
			SkuEsModel esModel = new SkuEsModel();
			BeanUtils.copyProperties(sku, esModel);
			esModel.setSkuPrice(sku.getPrice());
			esModel.setSkuImg(sku.getSkuDefaultImg());

			if (finalStockMap == null) {
				esModel.setHasStock(true);
			} else {
				esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
			}

			esModel.setHotScore(0L);

			// 品牌和分类名称
			BrandEntity brand = brandService.getById(sku.getBrandId());
			esModel.setBrandName(brand.getName());
			esModel.setBrandImg(brand.getLogo());

			CategoryEntity category = categoryService.getById(sku.getCatalogId());
			esModel.setCatalogName(category.getName());

			esModel.setAttrs(attrsList);

			return esModel;
		}).collect(Collectors.toList());

		// 发送给 glm-search 保存到es
		R r = searchFeignService.productUp(collect);
		if (r.getCode() == 0) {
			// 修改当前spu状态
			baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.UP.getCode());
		} else {
			// TODO 重复调用，接口幂等性；重试机制
		}


	}

	@Override
	public SpuInfoEntity getSpuBySkuId(Long id) {
		SkuInfoEntity sku = skuInfoService.getById(id);
		Long spuId = sku.getSpuId();
		return this.getById(spuId);
	}

}