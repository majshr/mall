package com.maj.mall.manage.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.maj.mall.bean.PmsBaseAttrInfo;
import com.maj.mall.bean.PmsBaseAttrValue;
import com.maj.mall.bean.PmsBaseSaleAttr;
import com.maj.mall.mapper.PmsBaseAttrInfoMapper;
import com.maj.mall.mapper.PmsBaseAttrValueMapper;
import com.maj.mall.mapper.PmsBaseSaleAttrMapper;
import com.maj.mall.service.PmsBaseAttrService;

import tk.mybatis.mapper.entity.Example;
/**
 * 
 * @author maj
 *
 */
@Service
public class PmsBaseAttrServiceImpl implements PmsBaseAttrService {

	@Autowired
	PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
	
	@Autowired
	PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
	
    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

	@Override
	public List<PmsBaseAttrInfo> getPmsBaseAttrInfo(String catalog3Id) {
		PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
		pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
		
		return pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
	}
	
	@Override
	public List<PmsBaseAttrValue> getPmsBaseAttrValue(String attrId) {
		PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
		pmsBaseAttrValue.setAttrId(attrId);
		
		return pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public String savePmsBaseAttr(PmsBaseAttrInfo pmsBaseAttrInfo) {
		if(StringUtils.isEmpty(pmsBaseAttrInfo.getId())) {
			// add
			pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);	
			List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
			for(PmsBaseAttrValue attrValue : attrValueList) {
				pmsBaseAttrValueMapper.insert(attrValue);
			}
		} 
		else {
			// update
			Example example = new Example(PmsBaseAttrInfo.class);
			example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
			pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);
			
			// 属性值信息，先删除，后插入
			// 删除
			PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
			pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
			pmsBaseAttrValueMapper.delete(pmsBaseAttrValue);
			List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
			// 插入
			for(PmsBaseAttrValue attrValue : attrValueList) {
				attrValue.setId(null);
				attrValue.setAttrId(pmsBaseAttrInfo.getId());
				pmsBaseAttrValueMapper.insert(attrValue);
			}
		}
		return pmsBaseAttrInfo.getId();
	}

    @Override
    public List<PmsBaseSaleAttr> getPmsBaseSaleAttr() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getPmsBaseSaleAttrByValueIds(Collection<String> ids) {
        return pmsBaseAttrInfoMapper.getPmsBaseSaleAttrByValueIds(ids);
    }

}
