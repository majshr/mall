package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.UmsMember;
import com.maj.mall.bean.UmsMemberReceiveAddress;

public interface UserService {
	/**
	 * 获取所有用户信息
	 * @return
	 */
	List<UmsMember> getAllUsers();
	
	/**
	 * 获取用户所有收货地址
	 * @param memberId
	 * @return
	 */
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId);

    /**
     * 根据id获取收货地址信息
     * 
     * @param receiveAddressId
     * @return UmsMemberReceiveAddress
     * @date: 2019年12月18日 上午10:40:13
     */
    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);

    /**
     * 登录验证，返回对应用户信息
     * @param umsMember
     * @return
     */
    UmsMember login(UmsMember umsMember);

    /**
     * 添加token到缓存
     * @param token
     * @param memberId
     */
    void addUserToken(String token, String memberId);

    /**
     * 检查oauth认证用户是否存在
     * 
     * @param umsCheck
     * @return UmsMember
     * @date: 2019年12月16日 下午3:05:59
     */
    UmsMember checkOauthUser(UmsMember umsCheck);

    /**
     * 添加oauth认证用户
     * 
     * @param umsMember
     * @return UmsMember
     * @date: 2019年12月16日 下午3:15:13
     */
    UmsMember addOauthUser(UmsMember umsMember);

}
