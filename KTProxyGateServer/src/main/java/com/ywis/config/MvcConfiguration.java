package com.ywis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ywis.interceptor.CheckInterceptor;

 
@EnableScheduling	
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(customInterceptor())
		.excludePathPatterns("/bootstrap/**","/dist/**",
				"/fonts/**","/js/**",
				"/css/**", "/fonts/**", "/plugin/**","/index.jsp","/.well-known/**");
	}

	
	@Bean
    public CheckInterceptor customInterceptor() {
        return new CheckInterceptor();
    }

}