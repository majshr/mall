package com.maj.mall.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class MallManageWebApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallManageWebApplication.class);
	    ConfigurableApplicationContext context = application.run(args);
	}
}
