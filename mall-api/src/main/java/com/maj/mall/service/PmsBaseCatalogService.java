package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.PmsBaseCatalog1;
import com.maj.mall.bean.PmsBaseCatalog2;
import com.maj.mall.bean.PmsBaseCatalog3;

/**
 * 分类service
 * @author maj
 *
 */
public interface PmsBaseCatalogService {
	/**
	 * 获取所有一级分类
	 * @return
	 */
    List<PmsBaseCatalog1> getCatalog1();

    /**
     * 根据一级分类获取二级分类
     * @param catalog1Id
     * @return
     */
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 根据二级分类获取三级分类
     * @param catalog2Id
     * @return
     */
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
