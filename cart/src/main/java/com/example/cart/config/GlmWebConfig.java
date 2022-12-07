package com.example.cart.config;

import com.example.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-10-12 10:28
 */
@Configuration
public class GlmWebConfig implements WebMvcConfigurer {
	@Resource
	CartInterceptor cartInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(cartInterceptor).addPathPatterns("/**");
	}
}
