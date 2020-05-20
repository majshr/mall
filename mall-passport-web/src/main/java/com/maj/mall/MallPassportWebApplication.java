package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class MallPassportWebApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallPassportWebApplication.class);
	    ConfigurableApplicationContext context = application.run(args);
	}
}
