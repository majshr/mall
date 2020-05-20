package com.maj.mall.service;

import java.math.BigDecimal;
import java.util.List;

import com.maj.mall.bean.PmsSkuInfo;

public interface SkuService {
	/**
	 * 保存sku信息
	 * @param pmsSkuInfo
	 */
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);
    
    /**
     * 根据Id获取详情
     * @param id
     * @return
     */
    PmsSkuInfo get(String id);

    /**
     * 查询产品有哪些销售属性值信息（库存单元，库存单元对应销售属性值）
     * @param productId
     * @return
     */
	List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);
	
	/**
	 * 查询所有商品信息
	 * @return
	 */
	List<PmsSkuInfo> getAllSku();

    /**
     * 验证价格是否正确（避免价格改变）
     * 
     * @param productSkuId
     * @param productPrice
     * @return boolean
     * @date: 2019年12月18日 上午11:26:58
     */
    boolean checkPrice(String productSkuId, BigDecimal productPrice);
}
