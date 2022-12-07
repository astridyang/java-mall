package com.example.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sally
 * @date 2022-08-22 9:40 AM
 */
@Configuration
@EnableTransactionManagement // 开启事务
@MapperScan("com.example.product.dao")
public class MyBatisConfig {
    @Bean
	public PaginationInterceptor paginationInterceptor(){
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		// true 返回首页，false 继续请求
		paginationInterceptor.setOverflow(true);
		// 设置最大单页限制数量，-1不限制
		paginationInterceptor.setLimit(100);
		return paginationInterceptor;
    }
}
