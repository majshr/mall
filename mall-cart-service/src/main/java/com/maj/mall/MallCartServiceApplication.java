package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.cart.mapper")
public class MallCartServiceApplication {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(MallCartServiceApplication.class);
        application.run(args);
	}
}
