package com.example.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.ware.entity.WareInfoEntity;
import com.example.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:28:35
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	FareVo getFare(Long addrId);
}

