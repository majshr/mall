package com.maj.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maj.mall.util.RedisUtil;

/**
 * 
 * @author maj
 *
 */
@Configuration
public class RedisConfig {
	// 读取配置文件中的redis的ip地址
	@Value("${local.redis.host:disabled}")
	private String host;
	
	@Value("${local.redis.port:0}")
	private int port;
	
	@Value("${local.redis.database:0}")
	private int database;

	@Bean
	public RedisUtil getRedisUtil() {
		if (host.equals("disabled")) {
			return null;
		}
		RedisUtil redisUtil = new RedisUtil();
		redisUtil.initPool(host, port, database);
		return redisUtil;
	}
}
