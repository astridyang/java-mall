package com.example.order.vo;

import com.example.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author sally
 * @date 2022-10-18 14:29
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code; // 错误状态码 0表示成功
}
