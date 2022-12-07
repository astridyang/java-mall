package com.example.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sally
 * @date 2022-10-09 15:02
 */
@ConfigurationProperties(prefix = "glm.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
	 Integer corePoolSize;
	 Integer maxPoolSize;
	 Integer keepAliveTime;
}
