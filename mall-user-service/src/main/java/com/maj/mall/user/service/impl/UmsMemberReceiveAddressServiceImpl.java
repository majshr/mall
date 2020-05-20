package com.maj.mall.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.maj.mall.bean.UmsMemberReceiveAddress;
import com.maj.mall.mapper.UmsMemberReceiveAddressMapper;
import com.maj.mall.service.UmsMemberReceiveAddressService;

@com.alibaba.dubbo.config.annotation.Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

	@Autowired
	UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
	
	@Override
	public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressesByMemberId(Long memberId) {
		/**
		 * 查询规则example为映射对象的Example
		 */
		//**********************查询方式1
		/*
		 * Example example = new Example(UmsMemberReceiveAddress.class);
		 * example.createCriteria().andEqualTo("memberId", memberId); 
		 * return umsMemberReceiveAddressMapper.selectByExample(example);
		 */
		
		//**********************查询方式2
		UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
		address.setMemberId(memberId);
		return umsMemberReceiveAddressMapper.select(address);
	}

}
