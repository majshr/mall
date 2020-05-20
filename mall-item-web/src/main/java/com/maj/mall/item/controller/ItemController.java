package com.maj.mall.item.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.bean.PmsProductSaleAttr;
import com.maj.mall.bean.PmsSkuInfo;
import com.maj.mall.bean.PmsSkuSaleAttrValue;
import com.maj.mall.service.SkuService;
import com.maj.mall.service.SpuService;

@Controller
public class ItemController {
	
    @Reference(timeout = 5000, retries = 0)
	private SkuService skuService;
	
    @Reference(timeout = 5000, retries = 0)
	private SpuService spuService;
	
	@RequestMapping("{skuId}.html")
	public String item(@PathVariable String skuId, ModelMap modelMap) {
		// 对象
		PmsSkuInfo pmsSkuInfo = skuService.get(skuId);
		modelMap.addAttribute("skuInfo", pmsSkuInfo);
		
		// 销售属性列表
		List<PmsProductSaleAttr> saleAttrs = 
				spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), skuId);
		modelMap.addAttribute("spuSaleAttrListCheckBySku", saleAttrs);
		
		// 查询当前sku的spu的其他sku的hash表集合
		List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
		
		Map<String, String> map = new HashMap<String, String>();
		for(PmsSkuInfo pmsSkuInfoObj : pmsSkuInfos) {
			String k = "";
			String v = pmsSkuInfoObj.getId();
			
			List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfoObj.getSkuSaleAttrValueList();
			for(PmsSkuSaleAttrValue skuSaleAttrValue : pmsSkuSaleAttrValueList) {
				// 使用管道符分隔，不建议使用","，使用，感觉像数组
				k += skuSaleAttrValue.getSaleAttrValueId() + "|";
			}
			map.put(k, v);
		}
		String skuSaleAttrHashJsonStr = com.alibaba.fastjson.JSON.toJSONString(map);
		modelMap.addAttribute("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);
		
		return "item";
	}
}
