package com.maj.mall.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisLock {
	@Autowired
	RedisUtil redisUtil;
	
	/**
	 * 删除锁lua脚本
	 */
    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }
    
    /**
     * 尝试获取锁
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public boolean tryLock(String key, String value, int expireTime) {
    	Jedis jedis = redisUtil.getJedis();
    	try {
    		// 设置成功表示获取锁成功
    		String ok = jedis.set(key, value, "nx", "px", expireTime);
    		if("OK".equals(ok)) {
    			return true;
    		}
    	}finally {
    		jedis.close();
    	}
    	
    	return false;
    }
    
    /**
     * 解锁
     * @param lockKey
     * @param requestId
     */
    public void unLock(String lockKey, String requestId) {
    	Jedis jedis = redisUtil.getJedis();
        List<String> keys = new ArrayList<String>(1);
        keys.add(lockKey);
        List<String> args = new ArrayList<>(1);
        args.add(requestId);
    	try {
            // 不关心执行结果，执行完后就当解锁了；删除成功说明解锁成功，删除失败说明时间过期解锁了
            jedis.eval(UNLOCK_LUA, keys, args);
    	}finally {
    		jedis.close();
    	}
    }
}
