package com.maj.mall.manage.controller;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.bean.PmsBaseSaleAttr;
import com.maj.mall.bean.PmsProductInfo;
import com.maj.mall.service.PmsBaseAttrService;
import com.maj.mall.service.SpuService;

/**
 * 商品spu系列信息controller
 * @author maj
 *
 */
@Controller
public class SpuController {
	
	@Reference
	SpuService spuService;
	
    @Reference
    PmsBaseAttrService pmsBaseAttrService;

	/**
	 * 获取产品信息
	 * @param catalog3Id
	 * @return
	 */
	@RequestMapping(value = "getPmsProductInfo", method = RequestMethod.GET)
	@ResponseBody
	public List<PmsProductInfo> getPmsProductInfo(@RequestParam String catalog3Id){
		return spuService.getPmsProductInfo(catalog3Id);
	}
	
	/**
	 * 获取所有基础销售属性
	 * @return
	 */
	@RequestMapping(value = "getPmsBaseSaleAttr", method = RequestMethod.GET)
	@ResponseBody
	public List<PmsBaseSaleAttr> getPmsBaseSaleAttr(){
        return pmsBaseAttrService.getPmsBaseSaleAttr();
	}
	
	/**
	 * 
	 */
	@RequestMapping(value = "savePmsProductInfo", method = RequestMethod.POST)
	@ResponseBody  //savePmsProductInfo
	public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
		return spuService.savePmsProductInfo(pmsProductInfo);
	}
	
	/**
	 * 
	 * @param multipartFile
	 * 	参数需要加注解，springmvc会根据参数名，将文件信息转为对象
	 * @return
	 */
	@RequestMapping("imageFileUpload")
	@ResponseBody
	public String imageFileUpload(@RequestParam("file")MultipartFile multipartFile) {
		
		// 文件信息存储到分布式文件服务器上
		
		// 返回上传文件的路径；之后保存数据库使用
		return "success";
	}
}
