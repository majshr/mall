package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//扫描mapper java接口
//@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.user.mapper")
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.mapper")
public class MallUserApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallUserApplication.class);
	    ConfigurableApplicationContext context = application.run(args);
	}

}