package com.example.order.dao;

import com.example.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:22:12
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

	void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);
}
