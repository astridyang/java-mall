package com.example.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sally
 * @date 2022-10-14 16:38
 */
@Configuration
public class GlmFeignConfig {
	@Bean("requestInterceptor")
	public RequestInterceptor requestInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate requestTemplate) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				if (requestAttributes != null) {
					HttpServletRequest request = requestAttributes.getRequest();
					String cookie = request.getHeader("Cookie");
					requestTemplate.header("Cookie", cookie);
				}
			}
		};
	}
}