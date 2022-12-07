package com.example.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.product.service.CategoryBrandRelationService;
import com.example.product.vo.Catelog2Vo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.CategoryDao;
import com.example.product.entity.CategoryEntity;
import com.example.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
	@Resource
	CategoryBrandRelationService categoryBrandRelationService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> listWithTree() {
		// 查出所有分类
		List<CategoryEntity> allCategory = baseMapper.selectList(null);
		// 找到所有一级分类
		// todo: stream,peek,comparator,Collectors
		List<CategoryEntity> level1Category = allCategory.stream()
				.filter(categoryEntity -> categoryEntity.getParentCid() == 0)
				.peek(menu -> menu.setChildren(getChildren(menu, allCategory)))
				.sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
				.collect(Collectors.toList());


		return level1Category;
	}

	@Override
	public void removeMenuByIds(List<Long> ids) {
		// todo 检查删除的菜单是否被其他地方引用
		baseMapper.deleteBatchIds(ids);
	}

	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> pathList = new ArrayList<>();
		List<Long> catelogPath = findCatelogPath(catelogId, pathList);
		Collections.reverse(catelogPath);
		return catelogPath.toArray(new Long[0]);
	}

	/**
	 * 级联更新所有关联数据
	 *
	 * @param category
	 */
	@CacheEvict(value = "category", allEntries = true)
	@Transactional
	@Override
	public void updateCascade(CategoryEntity category) {
		this.updateById(category);
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
	}

	@Cacheable(value = {"category"}, key = "#root.method.name",sync = true)
	@Override
	public List<CategoryEntity> getLevel1Categories() {
		System.out.println("获取商品一级分类");
		long s = System.currentTimeMillis();
		List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", "0"));
		System.out.println("消耗时间：" + (System.currentTimeMillis() - s));
		return categoryEntityList;
	}


	// public Map<String, List<Catelog2Vo>> getCatelogJson() {
	// 	// 加入缓存，json格式
	// 	ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
	// 	String catelogJSON = ops.get("catelogJSON");
	// 	Map<String, List<Catelog2Vo>> result;
	// 	if (StringUtils.hasLength(catelogJSON)) {
	// 		result = JSON.parseObject(catelogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
	// 		});
	// 	} else {
	// 		result = getCatelogJsonFromDB();
	// 		ops.set("catelogJSON", JSON.toJSONString(result),1, TimeUnit.DAYS);
	// 	}
	// 	return result;
	// }

	@Cacheable(value = "category", key = "#root.methodName",sync = true)
	@Override
	public Map<String, List<Catelog2Vo>> getCatelogJson() {
		System.out.println("查询数据库");
		// 查出所有分类
		List<CategoryEntity> allCatologList = baseMapper.selectList(null);
		// 1. 查出所有一级分类
		List<CategoryEntity> level1Categories = getCategoryByPid(allCatologList, 0L);
		Map<String, List<Catelog2Vo>> allCatelogJson = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
			// 2.查出所有二级分类
			List<Catelog2Vo> catelog2VoList = new ArrayList<>();
			List<CategoryEntity> level2Categories = getCategoryByPid(allCatologList, v.getCatId());
			if (level2Categories != null) {
				catelog2VoList = level2Categories.stream().map(l2 -> {
					Catelog2Vo catelog2Vo = new Catelog2Vo(l2.getCatId().toString(), l2.getName(), v.getCatId().toString(), null);
					// 3.查出所有三级分类
					List<CategoryEntity> level3Categories = getCategoryByPid(allCatologList, l2.getCatId());
					if (level3Categories != null) {
						List<Catelog2Vo.Catelog3Vo> catelog3VoList = level3Categories.stream().map(l3 -> new Catelog2Vo.Catelog3Vo(l3.getCatId().toString(), l3.getName(), l3.getParentCid().toString())).collect(Collectors.toList());
						catelog2Vo.setCatalog3List(catelog3VoList);
					}
					return catelog2Vo;
				}).collect(Collectors.toList());
			}
			return catelog2VoList;
		}));
		return allCatelogJson;
	}

	private List<CategoryEntity> getCategoryByPid(List<CategoryEntity> allCatologList, Long pid) {
		return allCatologList.stream().filter(item -> Objects.equals(item.getParentCid(), pid)).collect(Collectors.toList());
	}

	private List<Long> findCatelogPath(Long catelogId, List<Long> pathList) {
		pathList.add(catelogId);
		CategoryEntity category = this.getById(catelogId);
		if (category.getParentCid() != 0) {
			findCatelogPath(category.getParentCid(), pathList);
		}
		return pathList;
	}

	private List<CategoryEntity> getChildren(CategoryEntity menu, List<CategoryEntity> allCategory) {
		return allCategory.stream()
				.filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), menu.getCatId()))
				.peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, allCategory)))
				.sorted(Comparator.comparingInt(menu2 -> (menu2.getSort() == null ? 0 : menu2.getSort())))
				.collect(Collectors.toList());
	}

}