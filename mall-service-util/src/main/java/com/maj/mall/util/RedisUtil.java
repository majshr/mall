package com.maj.mall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis工具类
 * 
 * @author maj
 *
 */
public class RedisUtil {
    public static final String LUA_EQ_AND_DEL_SYNC;
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");    
        LUA_EQ_AND_DEL_SYNC = sb.toString();
    }
    
    
	private JedisPool jedisPool;

	/**
	 * 初始化池
	 * @param host
	 * @param port
	 * @param database
	 */
	public void initPool(String host, int port, int database) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(200);
		poolConfig.setMaxIdle(30);
		poolConfig.setBlockWhenExhausted(true);
		poolConfig.setMaxWaitMillis(10 * 1000);
		poolConfig.setTestOnBorrow(true);
		jedisPool = new JedisPool(poolConfig, host, port, 20 * 1000);
	}

	/**
	 * 从池中获取连接
	 * @return
	 */
	public Jedis getJedis() {
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}
}
