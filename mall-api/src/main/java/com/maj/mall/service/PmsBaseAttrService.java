package com.maj.mall.service;

import java.util.Collection;
import java.util.List;

import com.maj.mall.bean.PmsBaseAttrInfo;
import com.maj.mall.bean.PmsBaseAttrValue;
import com.maj.mall.bean.PmsBaseSaleAttr;

public interface PmsBaseAttrService {
	/**
	 * 根据3级catalog分类获取信息
	 * @param catalog3Id
	 * @return
	 */
	List<PmsBaseAttrInfo> getPmsBaseAttrInfo(String catalog3Id);
	
	/**
	 * 根据属性信息获取属性值信息
	 * @param attrId
	 * @return
	 */
	List<PmsBaseAttrValue> getPmsBaseAttrValue(String attrId);
	
	/**
	 * 保存信息
	 * @param pmsBaseAttrInfo
	 * @return
	 */
	String savePmsBaseAttr(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 获取所有基础销售属性
     * 
     * @return
     */
    List<PmsBaseSaleAttr> getPmsBaseSaleAttr();

    /**
     * 根据基础属性值查询所有基础属性信息
     * 
     * @param ids
     * @return List<PmsBaseAttrInfo>
     * @date: 2019年12月3日 下午7:56:14
     */
    List<PmsBaseAttrInfo> getPmsBaseSaleAttrByValueIds(Collection<String> ids);

}
