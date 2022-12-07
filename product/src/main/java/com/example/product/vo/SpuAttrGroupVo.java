package com.example.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author sally
 * @date 2022-10-08 17:22
 */
@Data
@ToString
public class SpuAttrGroupVo {
    private String groupName;
    private List<Attr> baseAttrs;
}
