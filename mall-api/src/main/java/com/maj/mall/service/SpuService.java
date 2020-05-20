package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.PmsProductInfo;
import com.maj.mall.bean.PmsProductSaleAttr;

/**
 * spu service
 * @author maj
 *
 */
public interface SpuService {
	/**
     * 根据三级分类获取产品信息
     * 
     * @param catalog3Id
     * @return
     */
	List<PmsProductInfo> getPmsProductInfo(String catalog3Id);
	
	/**
	 * 保存
	 * @param pmsProductInfo
	 */
	String savePmsProductInfo(PmsProductInfo pmsProductInfo);

	/**
	 * 获取产品销售属性信息（销售属性中也查询出了销售属性值）
	 * 一个产品可能有多个销售属性
	 * 如：
	 * 产品：小米6
	 * 销售属性：颜色，内存（每种属性又有多种值）
	 * @param productId
	 * @return
	 */
	List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId);
	
	/**
     * 获取产品销售属性信息（多种销售属性，如：颜色，内存等；每种销售属性对应一个销售值）
     * 
     * @param productId
     * @param skuId
     * @return
     */
	List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}
