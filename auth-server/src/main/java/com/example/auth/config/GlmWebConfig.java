package com.example.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sally
 * @date 2022-10-09 17:16
 */
@Configuration
public class GlmWebConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addViewController("/login.html").setViewName("login");
		registry.addViewController("/reg.html").setViewName("reg");
	}
}
