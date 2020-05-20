package com.maj.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class MallSearchControllerApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallSearchControllerApplication.class);
        application.run(args);
	}
}
