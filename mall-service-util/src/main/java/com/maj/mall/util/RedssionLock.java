package com.maj.mall.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedssionLock {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 根据name获取锁对象
     * @param name
     * @return
     */
    public RLock getLock(String name) {
        return redissonClient.getLock(name);
    }
    
    public static void main(String[] args) {
    	// 获取名称对应的锁
		RLock lock = new RedssionLock().getLock("test");
		
		lock.lock();
		lock.unlock();
	}
}
