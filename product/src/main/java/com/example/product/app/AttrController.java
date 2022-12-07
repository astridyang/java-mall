package com.example.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.product.entity.ProductAttrValueEntity;
import com.example.product.service.ProductAttrValueService;
import com.example.product.vo.AttrRespVo;
import com.example.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.product.service.AttrService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品属性
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
	@Autowired
	private AttrService attrService;

	@Resource
	ProductAttrValueService productAttrValueService;

	/**
	 * /product/attr/update/{spuId}
	 * @param spuId
	 * @return
	 */
	@PostMapping("/update/{spuId}")
	public R updateAttr(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entityList){
		productAttrValueService.updateAttr(spuId,entityList);
		return R.ok();
	}

	// base/listforspu/11
	@GetMapping("/base/listforspu/{spuId}")
	public R listForSpu(@PathVariable("spuId") Long spuId) {
		List<ProductAttrValueEntity> entityList = productAttrValueService.baseAttrListForSpu(spuId);
		return R.ok().put("data", entityList);
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = attrService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 列表
	 */
	@GetMapping("/{attrType}/list/{catelogId}")
	public R baseList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId, @PathVariable("attrType") String attrType) {
		PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrId}")
	public R info(@PathVariable("attrId") Long attrId) {
		AttrRespVo attr = attrService.getAttrInfo(attrId);

		return R.ok().put("attr", attr);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody AttrVo attr) {
		attrService.saveAttr(attr);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody AttrVo attr) {
		attrService.updateAttr(attr);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] attrIds) {
		attrService.removeByIds(Arrays.asList(attrIds));

		return R.ok();
	}

}
