package com.maj.mall.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.bean.UmsMember;
import com.maj.mall.service.UserService;

@Controller
public class UserController {
	/**
	 * 使用dubbo注解
	 */
	@Reference
	UserService userService;
	
	@RequestMapping("getAllUsers")
	@ResponseBody
	public List<UmsMember> getAllUsers() {
		return userService.getAllUsers();
	}
}
