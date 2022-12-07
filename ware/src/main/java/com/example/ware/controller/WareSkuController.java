package com.example.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.SkuHasStockTo;
import com.example.ware.exception.NoStockException;
import com.example.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ware.entity.WareSkuEntity;
import com.example.ware.service.WareSkuService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * 商品库存
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:28:35
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
	@Autowired
	private WareSkuService wareSkuService;

	@PostMapping("/lock/order")
	public R orderLockStock(@RequestBody WareSkuLockVo vo) {
		try {
			boolean result = wareSkuService.orderLockStock(vo);
			return R.ok();
		}catch (NoStockException e){
			return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(),BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
		}
	}

	@PostMapping("/hasStock")
	public R hasStock(@RequestBody List<Long> skuIds) {
		List<SkuHasStockTo> data = wareSkuService.getSkuHasStock(skuIds);
		return R.ok().put("data", data);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wareSkuService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		WareSkuEntity wareSku = wareSkuService.getById(id);

		return R.ok().put("wareSku", wareSku);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.save(wareSku);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.updateById(wareSku);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		wareSkuService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
