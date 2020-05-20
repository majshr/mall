package com.maj.mall.manage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.bean.PmsBaseCatalog1;
import com.maj.mall.bean.PmsBaseCatalog2;
import com.maj.mall.bean.PmsBaseCatalog3;
import com.maj.mall.service.PmsBaseCatalogService;

@Controller
@CrossOrigin // 允许跨域访问注解
public class CatalogController {
	
	/**
	 * 从Dubbo获取服务
	 */
	@Reference
	PmsBaseCatalogService catalogService;
	
	@RequestMapping("getCatalog1")
	@ResponseBody
	public List<PmsBaseCatalog1> getCatalog1() {
		return catalogService.getCatalog1();
	}
	
	@RequestMapping("getCatalog2")
	@ResponseBody
	public List<PmsBaseCatalog2> getCatalog2(@RequestParam String catalog1Id) {
		return catalogService.getCatalog2(catalog1Id);
	}
	
	@RequestMapping("getCatalog3")
	@ResponseBody
	public List<PmsBaseCatalog3> getCatalog3(@RequestParam String catalog2Id) {
		return catalogService.getCatalog3(catalog2Id);
	}
}
