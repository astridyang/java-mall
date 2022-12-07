package com.example.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.SkuHasStockTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.PageUtils;
import com.example.ware.entity.WareSkuEntity;
import com.example.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:28:35
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void addStock(Long skuId, Long wareId, Integer skuNum);

	List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);

	Boolean orderLockStock(WareSkuLockVo vo);

	void unLockStock(StockLockedTo to);

	void unLockStock(OrderTo order);
}

