package com.maj.mall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.maj.mall.interceptors.AuthInterceptor;

@Configuration
public class WebMvcConfiguration implements  WebMvcConfigurer{
	@Autowired
	AuthInterceptor authInterceptor; 
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 拦截所有地址  /**
        // 取消静态资源拦截，记着开始的 "/"
		registry.addInterceptor(authInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/error", "/css/**", "/img/**", "/js/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
