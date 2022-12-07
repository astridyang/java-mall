package com.example.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.example.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.product.entity.SpuInfoEntity;
import com.example.product.service.SpuInfoService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * spu信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:18
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
	@Autowired
	private SpuInfoService spuInfoService;

	@GetMapping("/skuId/{id}")
	public R getSpuBySkuId(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getSpuBySkuId(id);
		return R.ok().setData(spuInfo);
	}

	/**
	 * /9/up
	 * 上架
	 */
	@RequestMapping("/{spuId}/up")
	public R up(@PathVariable("spuId") Long spuId) {
		spuInfoService.up(spuId);
		return R.ok();
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuInfoService.queryByCondition(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

		return R.ok().put("spuInfo", spuInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SpuSaveVo vo) {
		// spuInfoService.save(spuInfo);
		spuInfoService.saveSpuInfo(vo);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SpuInfoEntity spuInfo) {
		spuInfoService.updateById(spuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		spuInfoService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
