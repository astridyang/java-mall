package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sally
 * @date 2022-09-13 4:41 PM
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
