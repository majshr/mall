package com.maj.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.maj.mall.bean.PmsSkuInfo;

import tk.mybatis.mapper.common.Mapper;

public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo>{

	List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(@Param("productId") String productId);
}
