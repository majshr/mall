package com.maj.mall.mapper;

import java.util.Collection;
import java.util.List;

import com.maj.mall.bean.PmsBaseAttrInfo;

import tk.mybatis.mapper.common.Mapper;

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> getPmsBaseSaleAttrByValueIds(Collection<String> ids);
}
