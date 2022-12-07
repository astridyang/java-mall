package com.example.product.feign.fallback;

import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import com.example.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author sally
 * @date 2022-11-14 14:47
 */
@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {
	@Override
	public R getSkuSeckillInfo(Long skuId) {
		log.info("熔断方法调用：getSkuSeckillInfo 。。。");
		return R.error(BizCodeEnum.TOO_MANY_EXCEPTION.getCode(), BizCodeEnum.TOO_MANY_EXCEPTION.getMsg());
	}
}
