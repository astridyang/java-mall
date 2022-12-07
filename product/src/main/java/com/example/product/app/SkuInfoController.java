package com.example.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.product.entity.SkuInfoEntity;
import com.example.product.service.SkuInfoService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * sku信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
	@Autowired
	private SkuInfoService skuInfoService;

	@RequestMapping("/{skuId}/price")
	public R getPrice(@PathVariable("skuId") Long skuId) {
		BigDecimal price = skuInfoService.getById(skuId).getPrice();
		return R.ok().put("data",price.toString());
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = skuInfoService.queryByCondition(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{skuId}")
	public R info(@PathVariable("skuId") Long skuId) {
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

		return R.ok().put("skuInfo", skuInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.save(skuInfo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.updateById(skuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] skuIds) {
		skuInfoService.removeByIds(Arrays.asList(skuIds));

		return R.ok();
	}

}
