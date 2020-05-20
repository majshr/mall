package com.maj.mall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
@SpringBootApplication
@EnableDubbo
public class ItemWebApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(ItemWebApplication.class);
        application.run(args);
	}

}
