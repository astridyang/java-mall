package com.example.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.ware.vo.MergePurchaseVo;
import com.example.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ware.entity.PurchaseEntity;
import com.example.ware.service.PurchaseService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * 采购信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:28:35
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 完成采购单
	 */
	@RequestMapping("/done")
	public R done(@RequestBody PurchaseDoneVo vo) {
		purchaseService.done(vo);
		return R.ok();
	}

	/**
	 * 领取采购单
	 */
	@RequestMapping("/receive")
	public R receive(@RequestBody List<Long> ids) {
		purchaseService.receive(ids);
		return R.ok();
	}

	/**
	 * 合并采购单
	 */
	@RequestMapping("/merge")
	public R merge(@RequestBody MergePurchaseVo vo) {
		purchaseService.merge(vo);
		return R.ok();
	}

	/**
	 * 未领取列表
	 */
	@RequestMapping("/unreceive/list")
	public R unreceiveList(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryUnreceiveList(params);

		return R.ok().put("page", page);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody PurchaseEntity purchase) {
		purchase.setCreateTime(new Date());
		purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody PurchaseEntity purchase) {
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
