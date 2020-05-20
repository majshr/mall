package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.maj.mall.mapper")
@EnableDubbo
public class MallManageServiceApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallManageServiceApplication.class);
        application.run(args);
	}
}
