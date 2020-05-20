package com.maj.mall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedssionConfig {
	// 读取配置文件中的redis的ip地址
	@Value("${local.redis.host:disabled}")
	private String host;
	
	@Value("${local.redis.port:0}")
	private int port;
	
	@Value("${local.redis.database:0}")
	private int database;

    @Bean
    RedissonClient redisson() {
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s", host,
        		port);
        config.useSingleServer().setAddress(redisUrl);
        config.useSingleServer().setDatabase(0);
        
        return Redisson.create(config);
    }
    
}
