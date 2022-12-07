package com.example.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.SkuHasStockTo;
import com.example.common.to.mq.StockDetailTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.R;
import com.example.common.constant.WareMq;
import com.example.ware.entity.WareOrderTaskDetailEntity;
import com.example.ware.entity.WareOrderTaskEntity;
import com.example.ware.exception.NoStockException;
import com.example.ware.feign.OrderFeignService;
import com.example.ware.feign.ProductFeignService;
import com.example.ware.service.WareOrderTaskDetailService;
import com.example.ware.service.WareOrderTaskService;
import com.example.ware.vo.OrderItemVo;
import com.example.ware.vo.OrderVo;
import com.example.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.ware.dao.WareSkuDao;
import com.example.ware.entity.WareSkuEntity;
import com.example.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
	@Resource
	WareSkuDao wareSkuDao;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	WareOrderTaskService wareOrderTaskService;

	@Resource
	WareOrderTaskDetailService wareOrderTaskDetailService;

	@Resource
	RabbitTemplate rabbitTemplate;

	@Resource
	OrderFeignService orderFeignService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
		// skuId:
		// wareId: 1
		String skuId = (String) params.get("skuId");
		if (StringUtils.hasLength(skuId)) {
			wrapper.eq("sku_id", skuId);
		}
		String wareId = (String) params.get("wareId");
		if (StringUtils.hasLength(wareId)) {
			wrapper.eq("ware_id", wareId);
		}
		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public void addStock(Long skuId, Long wareId, Integer skuNum) {
		// 1.数据库没有记录就新增
		List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
		if (list == null || list.size() == 0) {
			WareSkuEntity wareSkuEntity = new WareSkuEntity();
			wareSkuEntity.setSkuId(skuId);
			wareSkuEntity.setWareId(wareId);
			wareSkuEntity.setStock(skuNum);
			wareSkuEntity.setStockLocked(0);
			try {
				// 为了不让远程调用失败导致事务回滚使用try/catch
				// 远程调用product服务获取skuInfo
				R r = productFeignService.info(skuId);
				if (r.getCode() == 0) {
					Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
					wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
				}
			} catch (Exception ignored) {

			}
			wareSkuDao.insert(wareSkuEntity);
		} else {
			// 2.增加库存
			wareSkuDao.addStock(skuId, wareId, skuNum);
		}
	}

	@Override
	public List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds) {
		return skuIds.stream().map(skuId -> {
			SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
			skuHasStockTo.setSkuId(skuId);
			Long count = baseMapper.getSkuHasStock(skuId);
			skuHasStockTo.setHasStock(count != null && count > 0);
			return skuHasStockTo;
		}).collect(Collectors.toList());

	}

	// rollbackFor只要是运行时异常都会回滚
	@Transactional(rollbackFor = NoStockException.class)
	@Override
	public Boolean orderLockStock(WareSkuLockVo vo) {
		/**
		 * 保存库存工作单详情
		 */
		WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
		taskEntity.setOrderSn(vo.getOrderSn());
		wareOrderTaskService.save(taskEntity);


		// 1.找到每个商品在哪个仓库有库存
		List<OrderItemVo> locks = vo.getLocks();
		List<SkuWareHasStock> collect = locks.stream().map(item -> {
			SkuWareHasStock stock = new SkuWareHasStock();
			stock.setSkuId(item.getSkuId());
			stock.setNum(item.getCount());

			List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(item.getSkuId());
			stock.setWareIds(wareIds);
			return stock;
		}).collect(Collectors.toList());

		// 2.锁定库存
		for (SkuWareHasStock hasStock : collect) {
			boolean lockStock = false;
			Long skuId = hasStock.getSkuId();
			List<Long> wareIds = hasStock.getWareIds();
			if (wareIds == null || wareIds.size() == 0) {
				throw new NoStockException(skuId);
			}
			for (Long wareId : wareIds) {
				// 影响行数，1 for success,0 for failed
				Integer count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
				if (count == 1) {
					lockStock = true;
					// 给mq发消息
					WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), taskEntity.getId(), wareId, 1);
					wareOrderTaskDetailService.save(detailEntity);
					StockLockedTo lockedTo = new StockLockedTo();
					lockedTo.setId(detailEntity.getId());
					StockDetailTo stockDetailTo = new StockDetailTo();
					BeanUtils.copyProperties(detailEntity, stockDetailTo);
					lockedTo.setDetail(stockDetailTo);
					rabbitTemplate.convertAndSend(WareMq.STOCK_EVENT_EXCHANGE, WareMq.STOCK_LOCKED_KEY, lockedTo);
					break;
				} else {
					// 锁定失败，试下一个仓库
				}
			}
			if (!lockStock) {
				throw new NoStockException(skuId);
			}
		}
		// all stock locked success
		return true;
	}

	@Override
	public void unLockStock(StockLockedTo to) {
		StockDetailTo detail = to.getDetail();
		Long detailId = detail.getId();
		// release lock
		// 1. 查询数据库关于这个订单的锁定库存信息
		//  1）有，库存锁定成功
		//      查询订单情况：
		//          a）没有订单，必须解锁
		//          b）有订单；订单状态：已取消，解锁库存，没取消不能解锁
		//  2) 没有，库存锁定失败，库存回滚了，无需解锁
		WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);

		if (byId != null) {
			Long id = detail.getTaskId();
			WareOrderTaskEntity task = wareOrderTaskService.getById(id);
			R r = orderFeignService.getOrderByOrderSn(task.getOrderSn());
			if (r.getCode() == 0) {
				OrderVo data = r.getData(new TypeReference<OrderVo>() {
				});
				if (data == null || data.getStatus() == 4) {
					if (byId.getLockStatus() == 1) {
						// 当前库存详情单状态1：已锁定才能解锁
						unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
					}
				}
			} else {
				throw new RuntimeException("remote service failed.");
			}
		}
	}

	// 防止订单服务卡顿，库存消息优先到期没有解锁库存，导致卡顿的订单无法解锁库存
	@Transactional
	@Override
	public void unLockStock(OrderTo order) {
		String orderSn = order.getOrderSn();
		WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
		Long taskId = task.getId();
		// 按工作单id，查询没有解锁的库存，进行解锁
		List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId).eq("lock_status", 1));
		for (WareOrderTaskDetailEntity detail : list) {
			unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detail.getId());
		}
	}

	private void unLockStock(Long skuId, Long wareId, Integer skuNum, Long detailId) {
		// 解锁库存
		wareSkuDao.unLockStock(skuId, wareId, skuNum);
		// 更新库存工作单的状态
		WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity();
		taskDetail.setId(detailId);
		taskDetail.setLockStatus(2);
		wareOrderTaskDetailService.updateById(taskDetail);
	}

	@Data
	static
	class SkuWareHasStock {
		private Long SkuId;
		private Integer num;
		private List<Long> wareIds;
	}


}