package com.example.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sally
 * @date 2022-11-24 9:43
 */
// @Configuration
public class SeckillSentinelConfig {
	public SeckillSentinelConfig() {
		WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
			@Override
			public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException {
				R error = R.error(BizCodeEnum.TOO_MANY_EXCEPTION.getCode(), BizCodeEnum.TOO_MANY_EXCEPTION.getMsg());
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json");
				response.getWriter().write(JSON.toJSONString(error));
			}
		});
	}
}
