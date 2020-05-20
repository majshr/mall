package com.maj.mall.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.maj.mall.bean.PmsBaseCatalog1;
import com.maj.mall.bean.PmsBaseCatalog2;
import com.maj.mall.bean.PmsBaseCatalog3;
import com.maj.mall.mapper.PmsBaseCatalog1Mapper;
import com.maj.mall.mapper.PmsBaseCatalog2Mapper;
import com.maj.mall.mapper.PmsBaseCatalog3Mapper;
import com.maj.mall.service.PmsBaseCatalogService;

/**
 * Catalog分类
 * 
 * @author maj
 *
 */
@Service
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {

	@Autowired
	private PmsBaseCatalog1Mapper catalog1Mapper;
	
	@Autowired
	private PmsBaseCatalog2Mapper catalog2Mapper;

	@Autowired
	private PmsBaseCatalog3Mapper catalog3Mapper;

	
	@Override
	public List<PmsBaseCatalog1> getCatalog1() {
		return catalog1Mapper.selectAll();
	}

	@Override
	public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
		PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
		pmsBaseCatalog2.setCatalog1Id(catalog1Id);
		return catalog2Mapper.select(pmsBaseCatalog2);
	}

	@Override
	public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
		PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
		pmsBaseCatalog3.setCatalog2Id(catalog2Id);
		return catalog3Mapper.select(pmsBaseCatalog3);
	}

}
