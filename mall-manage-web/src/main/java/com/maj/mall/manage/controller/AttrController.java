package com.maj.mall.manage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.bean.PmsBaseAttrInfo;
import com.maj.mall.bean.PmsBaseAttrValue;
import com.maj.mall.bean.PmsBaseCatalog1;
import com.maj.mall.service.PmsBaseAttrService;

/**
 * 
 * @author maj
 *
 */
@Controller
@CrossOrigin
public class AttrController {
	
	@Reference
	private PmsBaseAttrService pmsBaseAttrService;
	
	@RequestMapping("getPmsBaseAttrInfo")
	@ResponseBody
	public List<PmsBaseAttrInfo> getPmsBaseAttrInfo(String catalog3Id) {
		return pmsBaseAttrService.getPmsBaseAttrInfo(catalog3Id);
	}
	
	@RequestMapping("getPmsBaseAttrValue")
	@ResponseBody
	public List<PmsBaseAttrValue> getPmsBaseAttrValue(String attrId){
		return  pmsBaseAttrService.getPmsBaseAttrValue(attrId);
	}
	
	/**
	 * 新加
	 * @param pmsBaseAttrInfo
	 * @return
	 */
	@RequestMapping(value = "savePmsBaseAttr", method = RequestMethod.POST)
	@ResponseBody
	public String savePmsBaseAttr(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
		
		return pmsBaseAttrService.savePmsBaseAttr(pmsBaseAttrInfo);
	}
}
