package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.PmsSearchSkuInfo;
import com.maj.mall.dto.PmsSearchParam;

public interface SearchService {
	/**
	 * 查询sku信息（从elsearch查询）
	 * @param pmsSearchParam
	 * @return
	 */
	List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
	
	/**
	 * 更新数据到elsearch
	 * @param pmsSearchSkuInfos
	 */
	public void save(List<PmsSearchSkuInfo> pmsSearchSkuInfos);
}
