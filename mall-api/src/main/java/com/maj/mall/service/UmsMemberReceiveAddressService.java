package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.UmsMemberReceiveAddress;

public interface UmsMemberReceiveAddressService {
	/**
	 * 获取用户收获地址
	 * @param memberId
	 * @return
	 */
	List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressesByMemberId(Long memberId);
}
