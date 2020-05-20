package com.maj.mall.manage.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maj.mall.bean.PmsBaseAttrInfo;
import com.maj.mall.service.PmsBaseAttrService;
import com.maj.mall.service.SkuService;

@Controller
public class TestController {
	@Autowired
	SkuService skuService;
	
    @Autowired
    PmsBaseAttrService pmsBaseAttrService;

	@RequestMapping("test")
	@ResponseBody
    public List<PmsBaseAttrInfo> test() {
		skuService.get("11");
        Collection<String> list = new HashSet();
        list.add("39");
        list.add("40");
        list.add("43");
        return pmsBaseAttrService.getPmsBaseSaleAttrByValueIds(list);
	}
}

