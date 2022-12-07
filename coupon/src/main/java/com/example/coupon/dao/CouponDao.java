package com.example.coupon.dao;

import com.example.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:07:57
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
