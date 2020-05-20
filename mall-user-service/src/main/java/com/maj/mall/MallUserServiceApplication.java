package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;

@SpringBootApplication
//扫描mapper java接口
//@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.user.mapper")
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.mapper")
//@EnableDubboConfiguration
@EnableDubbo
public class MallUserServiceApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallUserServiceApplication.class);
	    ConfigurableApplicationContext context = application.run(args);
	}
}
