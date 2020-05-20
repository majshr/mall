package com.maj.mall.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.maj.mall.bean.PmsProductInfo;
import com.maj.mall.bean.PmsProductSaleAttr;
import com.maj.mall.bean.PmsProductSaleAttrValue;
import com.maj.mall.mapper.PmsBaseSaleAttrMapper;
import com.maj.mall.mapper.PmsProductInfoMapper;
import com.maj.mall.mapper.PmsProductSaleAttrMapper;
import com.maj.mall.mapper.PmsProductSaleAttrValueMapper;
import com.maj.mall.service.SpuService;

/**
 * 产品
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年11月28日 下午5:45:37
 */
@Service
public class SpuServiceImpl implements SpuService {

	@Reference
	PmsProductInfoMapper pmsProductInfoMapper;
	
	/**
	 * 基础销售属性Mapper
	 */
	@Autowired
	PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
	
	@Autowired
	PmsProductSaleAttrMapper productSaleAttrMapper;
	
	@Autowired
	PmsProductSaleAttrValueMapper productSaleAttrValueMapper; 
	
	@Override
	public List<PmsProductInfo> getPmsProductInfo(String catalog3Id) {
		PmsProductInfo pmsProductInfo = new PmsProductInfo();
		pmsProductInfo.setCatalog3Id(catalog3Id);
		
		return pmsProductInfoMapper.select(pmsProductInfo);
	}

	@Override
	public String savePmsProductInfo(PmsProductInfo pmsProductInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId) {
		PmsProductSaleAttr saleAttr = new PmsProductSaleAttr();
		saleAttr.setProductId(productId);
		List<PmsProductSaleAttr> saleAttrs = productSaleAttrMapper.select(saleAttr);
		if(saleAttrs == null) {
			return saleAttrs;
		}
		
		saleAttrs.forEach((saleAttrObj)->{
			PmsProductSaleAttrValue productSaleAttrValue = new PmsProductSaleAttrValue(); 
			productSaleAttrValue.setProductId(productId);
			productSaleAttrValue.setSaleAttrId(saleAttrObj.getSaleAttrId());
			saleAttrObj.setSpuSaleAttrValueList(productSaleAttrValueMapper.select(productSaleAttrValue));
		});
		
		return saleAttrs;
	}

	@Override
	public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
		List<PmsProductSaleAttr> saleAttrs = productSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId, skuId);
		return saleAttrs;
	}
	
	

}
