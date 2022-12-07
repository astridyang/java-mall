package com.example.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.product.entity.AttrEntity;
import com.example.product.service.AttrAttrgroupRelationService;
import com.example.product.service.AttrService;
import com.example.product.service.CategoryService;
import com.example.product.vo.AttrGroupWithAttrVo;
import com.example.product.vo.AttrRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.product.entity.AttrGroupEntity;
import com.example.product.service.AttrGroupService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * 属性分组
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-12 16:49:19
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
	@Autowired
	private AttrGroupService attrGroupService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private AttrService attrService;

	@Resource
	private AttrAttrgroupRelationService relationService;

	/**
	 * 获取分类下所有分组&关联属性
	 * GET
	 * /product/attrgroup/{catelogId}/withattr
	 */
	@GetMapping("/{catelogId}/withattr")
	public R attrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
		List<AttrGroupWithAttrVo> vos = attrGroupService.getGroupWithAttrsByCateId(catelogId);
		return R.ok().put("data", vos);
	}


	/**
	 * 获取属性分组的关联的所有属性
	 */
	@GetMapping("/{attrgroupId}/attr/relation")
	public R getAttrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
		List<AttrEntity> entityList = attrService.getRelationAttr(attrgroupId);
		return R.ok().put("data", entityList);
	}

	/**
	 * 获取属性分组没有关联的其他属性
	 * GET
	 * /product/attrgroup/{attrgroupId}/noattr/relation
	 */
	@GetMapping("/{attrgroupId}/noattr/relation")
	public R noRelationAttr(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
		PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
		return R.ok().put("page", page);
	}

	/**
	 * 添加属性与分组关联关系
	 * POST
	 * /product/attrgroup/attr/relation
	 */
	@PostMapping("/attr/relation")
	public R addRelation(@RequestBody List<AttrRelationVo> vos){
		relationService.saveRelation(vos);
		return R.ok();
	}


	/**
	 * 删除属性与分组的关联关系
	 * /product/attrgroup/attr/relation/delete
	 */
	@PostMapping("/attr/relation/delete")
	public R deleteRelation(@RequestBody AttrRelationVo[] vos) {
		attrService.deleteRelation(vos);
		return R.ok();
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list/{cateId}")
	public R list(@RequestParam Map<String, Object> params, @PathVariable("cateId") Long cateId) {
		// PageUtils page = attrGroupService.queryPage(params);
		PageUtils page = attrGroupService.queryPage(params, cateId);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		Long catelogId = attrGroup.getCatelogId();
		Long[] catelogPath = categoryService.findCatelogPath(catelogId);
		attrGroup.setCatelogPath(catelogPath);
		return R.ok().put("attrGroup", attrGroup);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.save(attrGroup);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

		return R.ok();
	}

}
