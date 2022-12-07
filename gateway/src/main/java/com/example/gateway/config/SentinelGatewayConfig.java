package com.example.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author sally
 * @date 2022-11-24 11:37
 */
@Configuration
public class SentinelGatewayConfig {

	public SentinelGatewayConfig() {
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				R error = R.error(BizCodeEnum.TOO_MANY_EXCEPTION.getCode(), BizCodeEnum.TOO_MANY_EXCEPTION.getMsg());
				String jsonString = JSON.toJSONString(error);
				Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(jsonString), String.class);
				return body;
			}
		});
	}

}
