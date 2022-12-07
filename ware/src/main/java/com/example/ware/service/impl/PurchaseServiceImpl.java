package com.example.ware.service.impl;

import com.example.common.constant.WareConstant;
import com.example.ware.entity.PurchaseDetailEntity;
import com.example.ware.service.PurchaseDetailService;
import com.example.ware.service.WareSkuService;
import com.example.ware.vo.MergePurchaseVo;
import com.example.ware.vo.PurchaseDoneItem;
import com.example.ware.vo.PurchaseDoneVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.ware.dao.PurchaseDao;
import com.example.ware.entity.PurchaseEntity;
import com.example.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

	@Resource
	PurchaseDetailService purchaseDetailService;

	@Resource
	WareSkuService wareSkuService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryUnreceiveList(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
		);

		return new PageUtils(page);

	}

	@Transactional
	@Override
	public void merge(MergePurchaseVo vo) {
		Long purchaseId = vo.getPurchaseId();
		if (purchaseId == null) {
			// 新建采购单
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setCreateTime(new Date());
			purchaseEntity.setUpdateTime(new Date());
			purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
			this.save(purchaseEntity);
			purchaseId = purchaseEntity.getId();
		}
		// TODO 确认采购单状态是0或者1才能合并
		// 合并采购单，更新采购单修改日期
		List<Long> items = vo.getItems();
		Long finalPurchaseId = purchaseId;
		List<PurchaseDetailEntity> collect = items.stream().map(item -> {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
			detailEntity.setId(item);
			detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
			detailEntity.setPurchaseId(finalPurchaseId);
			return detailEntity;
		}).collect(Collectors.toList());

		purchaseDetailService.updateBatchById(collect);

		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(purchaseId);
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);

	}

	@Transactional
	@Override
	public void receive(List<Long> ids) {
		// 1. 确认当前采购单是新建或者已分配状态
		// todo 直接 update in ids？
		List<PurchaseEntity> collect = ids.stream().map(this::getById)
				.filter(item -> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
				.peek(item -> {
					item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
					item.setUpdateTime(new Date());
				})
				.collect(Collectors.toList());
		// 2. 改变采购单的状态
		if (collect.size() > 0) {
			this.updateBatchById(collect);
			// 3. 改变采购项的状态
			collect.forEach(item -> {
				List<PurchaseDetailEntity> entityList = purchaseDetailService.listByPurchaseId(item.getId());
				if (entityList.size() > 0) {
					List<PurchaseDetailEntity> collect1 = entityList.stream().map(entity -> {
						PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
						detailEntity.setId(entity.getId());
						detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
						return detailEntity;
					}).collect(Collectors.toList());
					purchaseDetailService.updateBatchById(collect1);
				}
			});
		}
	}

	@Transactional
	@Override
	public void done(PurchaseDoneVo vo) {
		Long id = vo.getId();

		// 2. 改变采购项的状态
		boolean flag = true;
		List<PurchaseDoneItem> items = vo.getItems();
		List<PurchaseDetailEntity> detailEntities = new ArrayList<>();
		for (PurchaseDoneItem item : items) {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
			if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.FAIL.getCode()){
				flag = false;
			}else{
				// 3. 将成功采购的进行入库
				PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
				wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
			}
			detailEntity.setId(item.getItemId());
			detailEntity.setStatus(item.getStatus());
			detailEntities.add(detailEntity);

		}
		purchaseDetailService.updateBatchById(detailEntities);
		// 1. 改变采购单状态
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(id);
		purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);

	}

}