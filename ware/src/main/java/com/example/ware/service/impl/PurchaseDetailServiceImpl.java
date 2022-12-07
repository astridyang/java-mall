package com.example.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.ware.dao.PurchaseDetailDao;
import com.example.ware.entity.PurchaseDetailEntity;
import com.example.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		// key:
		// status:
		// wareId:
		QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (StringUtils.hasLength(key)) {
			wrapper.and(w -> {
				w.eq("purchase_id", key).or().eq("sku_id", key);
			});
		}
		String status = (String) params.get("status");
		if (StringUtils.hasLength(status)) {
			wrapper.eq("status", status);
		}
		String wareId = (String) params.get("wareId");
		if (StringUtils.hasLength(wareId)) {
			wrapper.eq("ware_id", wareId);
		}
		IPage<PurchaseDetailEntity> page = this.page(
				new Query<PurchaseDetailEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<PurchaseDetailEntity> listByPurchaseId(Long id) {
		return this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
	}

}