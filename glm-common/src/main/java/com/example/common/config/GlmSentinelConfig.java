package com.example.common.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sally
 * @date 2022-11-14 10:47
 */
@Configuration
public class GlmSentinelConfig implements BlockExceptionHandler {
	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
		R r = R.error(BizCodeEnum.TOO_MANY_EXCEPTION.getCode(), BizCodeEnum.TOO_MANY_EXCEPTION.getMsg());
		httpServletResponse.setCharacterEncoding("utf-8");
		httpServletResponse.setContentType("application/json");
		httpServletResponse.getWriter().write(JSON.toJSONString(r));
	}


	// public GlmSentinelConfig() {
	// 	WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
	// 		@Override
	// 		public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
	//
	// 		}
	// 	});
	// }


}