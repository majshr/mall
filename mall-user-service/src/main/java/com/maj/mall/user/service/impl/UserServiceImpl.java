package com.maj.mall.user.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.maj.mall.bean.UmsMember;
import com.maj.mall.bean.UmsMemberReceiveAddress;
import com.maj.mall.mapper.UmsMemberReceiveAddressMapper;
import com.maj.mall.mapper.UserMapper;
import com.maj.mall.service.UserService;
import com.maj.mall.util.RedisUtil;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author maj
 * 使用dubbo的@Service注解
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UmsMemberReceiveAddressMapper addressMapper;
	
	@Autowired
	RedisUtil redisUtil;
	
	@Override
	public List<UmsMember> getAllUsers() {
		return userMapper.selectAll();
	}

	@Override
	public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId) {
		UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
		address.setMemberId(memberId);
		
		return addressMapper.select(address);
	}
	
    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        return addressMapper.selectByPrimaryKey(receiveAddressId);
    }

	@Override
	public UmsMember login(UmsMember umsMember) {
		Jedis jedis = redisUtil.getJedis();
		try {
            // 缓存中有，查询缓存
			String umsMemberStr = jedis.get(getMemberCacheKey(umsMember));
			if(!StringUtils.isEmpty(umsMemberStr)) {
				UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
				return umsMemberFromCache;
			}
			
            // 缓存中没有，查数据库，设置到缓存
			UmsMember umsMemberFromDb = getUmsMemberFromDb(umsMember);
			if(umsMemberFromDb != null) {
				jedis.setex(getMemberCacheKey(umsMember), 60*60*24, JSON.toJSONString(umsMemberFromDb));
			}
			return umsMemberFromDb;
		}finally {
			jedis.close();
		}
	}
	
	/**
     * 获取本地用户缓存redis的key
     * 
     * @param umsMember
     * @return
     */
	private String getMemberCacheKey(UmsMember umsMember) {
        return "user:" + umsMember.getPassword() + umsMember.getUsername() + ":info";
	}
	
    /**
     * 获取oauth用户缓存redis key
     * 
     * @param umsMember
     * @return String
     * @date: 2019年12月16日 下午3:12:22
     */
    private String getOauthMemberCacheKey(UmsMember umsMember) {
        return "user:oauth:" + umsMember.getSourceType() + ":" + umsMember.getSourceUid() + "info";
    }

	@Override
	public void addUserToken(String token, String memberId) {
		Jedis jedis = redisUtil.getJedis();
		
		try {
			jedis.setex(getMemberTokenCacheKey(memberId), 60 * 60 * 2, token);
		} finally {
			jedis.close();
		}
		
	}
	
	/**
	 * 获取用户token key
	 * @param memberId
	 * @return
	 */
	private String getMemberTokenCacheKey(String memberId) {
		return "user:" + memberId + ":token";
	}
	
	/**
	 * 从数据库查询
	 * @param umsMember
	 * @return
	 */
	private UmsMember getUmsMemberFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers = userMapper.select(umsMember);

        if (umsMembers != null && !umsMembers.isEmpty()) {
            return umsMembers.get(0);
        }

        return null;

    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        return getUmsMemberFromDb(umsCheck);
    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        userMapper.insert(umsMember);
        return umsMember;
    }

}




















