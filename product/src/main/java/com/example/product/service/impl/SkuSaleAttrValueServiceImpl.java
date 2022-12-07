package com.example.product.service.impl;

import com.example.product.vo.SpuSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.product.dao.SkuSaleAttrValueDao;
import com.example.product.entity.SkuSaleAttrValueEntity;
import com.example.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<SpuSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
		SkuSaleAttrValueDao dao = this.baseMapper;
		return dao.getSaleAttrBySpuId(spuId);
	}

	@Override
	public List<String> getAttrString(Long skuId) {

		SkuSaleAttrValueDao dao = this.baseMapper;
		return dao.getAttrString(skuId);
	}

}