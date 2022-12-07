package com.example.coupon.service.impl;

import com.example.coupon.entity.SeckillSkuRelationEntity;
import com.example.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.coupon.dao.SeckillSessionDao;
import com.example.coupon.entity.SeckillSessionEntity;
import com.example.coupon.service.SeckillSessionService;

import javax.annotation.Resource;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
	@Resource
	SeckillSkuRelationService seckillSkuRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SeckillSessionEntity> page = this.page(
				new Query<SeckillSessionEntity>().getPage(params),
				new QueryWrapper<SeckillSessionEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<SeckillSessionEntity> getLatest3DaySession() {

		List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
		if (list != null && list.size() > 0) {
			List<SeckillSessionEntity> collect = list.stream().map(item -> {
				Long id = item.getId();
				List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
				item.setRelationSkus(relationEntities);
				return item;
			}).collect(Collectors.toList());
			return collect;
		}
		return null;
	}

	private String startTime() {
		LocalDate now = LocalDate.now();
		LocalTime min = LocalTime.MIN;
		LocalDateTime start = LocalDateTime.of(now, min);
		return start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	private String endTime() {
		LocalDate now = LocalDate.now();
		LocalDateTime end = LocalDateTime.of(now.plusDays(2), LocalTime.MAX);
		return end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}