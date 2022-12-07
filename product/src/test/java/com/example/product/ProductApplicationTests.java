package com.example.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.product.dao.AttrGroupDao;
import com.example.product.dao.SkuSaleAttrValueDao;
import com.example.product.entity.BrandEntity;
import com.example.product.service.BrandService;
import com.example.product.service.CategoryService;
import com.example.product.vo.SpuAttrGroupVo;
import com.example.product.vo.SpuSaleAttrVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class ProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Resource
	CategoryService categoryService;

	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Resource
	RedissonClient redissonClient;

	@Resource
	AttrGroupDao attrGroupDao;

	@Resource
	SkuSaleAttrValueDao skuSaleAttrValueDao;

	@Test
	public void testGetSaleAttr(){
		// saleAttrVos = [SpuSaleAttrVo(attrId=5, attrName=颜色, attrValues=[SpuSaleAttrWithSkuId(attrValue=白, skuIds=15,16,17,18), SpuSaleAttrWithSkuId(attrValue=红, skuIds=7,8,9,10), SpuSaleAttrWithSkuId(attrValue=黑, skuIds=11,12,13,14)]), SpuSaleAttrVo(attrId=9, attrName=版本, attrValues=[SpuSaleAttrWithSkuId(attrValue=128GB, skuIds=7,11,15), SpuSaleAttrWithSkuId(attrValue=1T, skuIds=10,14,18), SpuSaleAttrWithSkuId(attrValue=256GB, skuIds=8,12,16), SpuSaleAttrWithSkuId(attrValue=512GB, skuIds=9,13,17)])]

		List<SpuSaleAttrVo> saleAttrVos = skuSaleAttrValueDao.getSaleAttrBySpuId(9L);
		System.out.println("saleAttrVos = " + saleAttrVos);
	}

	@Test
	public void testGetAttrGroup(){
		// groupWithAttrs = [SpuAttrGroupVo(groupName=基本信息, baseAttrs=[Attr(attrId=null, attrName=上市年份, attrValue=2021), Attr(attrId=null, attrName=机身材质工艺, attrValue=陶瓷;玻璃)]), SpuAttrGroupVo(groupName=主体, baseAttrs=[Attr(attrId=null, attrName=CPU品牌, attrValue=Apple), Attr(attrId=null, attrName=CPU型号, attrValue=A15)])]

		List<SpuAttrGroupVo> groupWithAttrs = attrGroupDao.getGroupWithAttrs(9L, 225L);
		System.out.println("groupWithAttrs = " + groupWithAttrs);
	}

	@Test
	public void testRedisson(){
		System.out.println(redissonClient);
	}

	@Test
	public void testRedis() {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello", "world_" + UUID.randomUUID());
		System.out.println(ops.get("hello"));
	}

	@Test
	public void testFindCatelogPath() {
		Long[] catelogPath = categoryService.findCatelogPath(225L);
		log.info("catelogPath: {}", Arrays.asList(catelogPath));
	}

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("apple");
//        brandService.save(brandEntity);
//        System.out.println("save success");
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("bad apple");
//        brandService.updateById(brandEntity);
		for (BrandEntity brand : brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L))) {
			System.out.println(brand);
		}

	}

}
