package com.example.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author sally
 * @date 2022-10-11 14:37
 */
@EnableRedisHttpSession
@Configuration
public class GlmSessionConfig {

	@Bean
	public RedisSerializer<Object> redisSerializer(){
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean
	public CookieSerializer cookieSerializer(){
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setCookieName("GLMSESSION");
		cookieSerializer.setDomainName("gmall.com");

		return cookieSerializer;
	}

}
