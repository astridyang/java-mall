package com.example.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.ware.feign.MemberFeignService;
import com.example.ware.vo.FareVo;
import com.example.ware.vo.MemberAddressVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.ware.dao.WareInfoDao;
import com.example.ware.entity.WareInfoEntity;
import com.example.ware.service.WareInfoService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
	@Resource
	MemberFeignService memberFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (StringUtils.hasLength(key)) {
			wrapper.eq("id", key)
					.or().like("name", key)
					.or().like("address", key)
					.or().like("areacode", key);
		}
		IPage<WareInfoEntity> page = this.page(
				new Query<WareInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public FareVo getFare(Long addrId) {
		FareVo fareVo = new FareVo();

		String fare;
		if (addrId % 2 == 0) {
			fare = "5";
		} else {
			fare = "10";
		}
		fareVo.setFare(new BigDecimal(fare));

		// query user address
		R r = memberFeignService.info(addrId);
		MemberAddressVo address = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
		});
		fareVo.setAddress(address);
		return fareVo;
	}

}