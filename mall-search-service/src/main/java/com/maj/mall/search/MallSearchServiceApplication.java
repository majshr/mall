package com.maj.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

// exlude，配置启动时，不自动配置数据库
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableDubbo
public class MallSearchServiceApplication {
	public static void main(String[] args) {
	    SpringApplication application = new SpringApplication(MallSearchServiceApplication.class);
        application.run(args);
	}
}
