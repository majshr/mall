package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class MallCartWebApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallCartWebApplication.class);
        application.run(args);
	}
}
