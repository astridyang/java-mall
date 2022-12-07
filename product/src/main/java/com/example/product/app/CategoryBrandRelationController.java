package com.example.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.product.entity.BrandEntity;
import com.example.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.product.entity.CategoryBrandRelationEntity;
import com.example.product.service.CategoryBrandRelationService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;

	/**
	 * 获取分类关联的品牌
	 * GET
	 * /product/categorybrandrelation/brands/list
	 */
	@GetMapping("/brands/list")
	public R brandList(@RequestParam("catId") Long catId) {
		List<BrandEntity> brandList = categoryBrandRelationService.getBrandListByCatId(catId);
		List<BrandVo> vos = brandList.stream().map(item -> {
			BrandVo brandVo = new BrandVo();
			brandVo.setBrandId(item.getBrandId());
			brandVo.setBrandName(item.getName());
			return brandVo;
		}).collect(Collectors.toList());
		return R.ok().put("data", vos);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = categoryBrandRelationService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 获取当前品牌关联的所有分类列表
	 */
	@GetMapping("/catelog/list")
	public R catelogList(@RequestParam("brandId") Long brandId) {
		List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
				new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
		return R.ok().put("data", data);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

		return R.ok().put("categoryBrandRelation", categoryBrandRelation);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		categoryBrandRelationService.updateById(categoryBrandRelation);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
