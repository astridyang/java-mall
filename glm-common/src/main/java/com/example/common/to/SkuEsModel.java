package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sally
 * @date 2022-09-19 17:45
 */
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private String brandName;
    private String brandImg;
    private Long catalogId;
    private String catalogName;
    private List<Attrs> attrs;

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
