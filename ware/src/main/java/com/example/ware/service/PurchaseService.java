package com.example.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.ware.entity.PurchaseEntity;
import com.example.ware.vo.MergePurchaseVo;
import com.example.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:28:35
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryUnreceiveList(Map<String, Object> params);

	void merge(MergePurchaseVo vo);

	void receive(List<Long> ids);

	void done(PurchaseDoneVo vo);
}

