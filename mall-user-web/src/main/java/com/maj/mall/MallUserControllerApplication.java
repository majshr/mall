package com.maj.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;

@SpringBootApplication
@EnableDubbo
public class MallUserControllerApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallUserControllerApplication.class);
	    ConfigurableApplicationContext context = application.run(args);
	}
}
