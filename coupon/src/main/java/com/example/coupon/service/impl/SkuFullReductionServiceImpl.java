package com.example.coupon.service.impl;

import com.example.common.to.MemberPrice;
import com.example.common.to.SkuReductionTo;
import com.example.coupon.entity.MemberPriceEntity;
import com.example.coupon.entity.SkuLadderEntity;
import com.example.coupon.service.MemberPriceService;
import com.example.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.coupon.dao.SkuFullReductionDao;
import com.example.coupon.entity.SkuFullReductionEntity;
import com.example.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Resource
	SkuLadderService skuLadderService;

	@Resource
	MemberPriceService memberPriceService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuFullReductionEntity> page = this.page(
				new Query<SkuFullReductionEntity>().getPage(params),
				new QueryWrapper<SkuFullReductionEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveReduction(SkuReductionTo skuReductionTo) {
		// sku的优惠、满减信息 glm_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
		// 1. 保存阶梯价格
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
		skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
		skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
		skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
		if (skuReductionTo.getFullCount() > 0) {
			skuLadderService.save(skuLadderEntity);
		}

		// 2.保存满减信息
		SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo, reductionEntity);
		if (reductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
			this.save(reductionEntity);
		}

		// 3.保存会员价格
		List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
		if (memberPrice != null && memberPrice.size() != 0) {
			List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
				MemberPriceEntity priceEntity = new MemberPriceEntity();
				priceEntity.setSkuId(skuReductionTo.getSkuId());
				priceEntity.setMemberLevelId(item.getId());
				priceEntity.setMemberLevelName(item.getName());
				priceEntity.setMemberPrice(item.getPrice());
				priceEntity.setAddOther(1);
				return priceEntity;
			}).filter(item -> item.getMemberPrice().compareTo(new BigDecimal("0")) > 0).collect(Collectors.toList());
			memberPriceService.saveBatch(collect);
		}
	}

}