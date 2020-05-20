package com.maj.mall.manage.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.maj.mall.bean.PmsSkuAttrValue;
import com.maj.mall.bean.PmsSkuImage;
import com.maj.mall.bean.PmsSkuInfo;
import com.maj.mall.bean.PmsSkuSaleAttrValue;
import com.maj.mall.constant.Constant;
import com.maj.mall.mapper.PmsSkuAttrValueMapper;
import com.maj.mall.mapper.PmsSkuImageMapper;
import com.maj.mall.mapper.PmsSkuInfoMapper;
import com.maj.mall.mapper.PmsSkuSaleAttrValueMapper;
import com.maj.mall.service.SkuService;
import com.maj.mall.util.CloseUtil;
import com.maj.mall.util.RedisLock;
import com.maj.mall.util.RedisUtil;

import redis.clients.jedis.Jedis;

@Service
public class SkuServiceImpl implements SkuService {

	@Autowired
	PmsSkuInfoMapper pmsSkuInfoMapper;

	@Autowired
	PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

	@Autowired
	RedisUtil redisUtil;
	
	@Autowired
	RedisLock redisLock;

	@Autowired
	PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

	@Autowired
	PmsSkuImageMapper pmsSkuImageMapper;

	@Override
	public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

		// 插入skuInfo
		int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
		String skuId = pmsSkuInfo.getId();

		// 插入平台属性关联
		List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
		for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
			pmsSkuAttrValue.setSkuId(skuId);
			pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
		}

		// 插入销售属性关联
		List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
		for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
			pmsSkuSaleAttrValue.setSkuId(skuId);
			pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
		}

		// 插入图片信息
		List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
		for (PmsSkuImage pmsSkuImage : skuImageList) {
			pmsSkuImage.setSkuId(skuId);
			pmsSkuImageMapper.insertSelective(pmsSkuImage);
		}

	}
	
	/**
	 * 获取SkuInfo的缓存key
	 * @param id
	 * @return
	 */
	private String getSkuInfoCacheKey(String id) {
		return "sku:" + id + ":info";
	}
	
	/**
	 * 获取更新缓存skuInfo信息的分布式锁key
	 * @param id
	 * @return
	 */
	private String getSkuInfoLockStr(String id) {
		return "sku:" + id + ":lock";
	}

    public static final Logger LOG = LoggerFactory.getLogger(SkuServiceImpl.class);

	@Override
	public PmsSkuInfo get(String id) {
		PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
		
		Jedis jedis = null;
		try {
		    jedis = redisUtil.getJedis();
		    String skuKey = getSkuInfoCacheKey(id);
	        String skuJson = jedis.get(skuKey);
	        
	        // 缓存击穿的数据
	        if(Constant.EMPTY_STR.equals(skuJson)) {
	            return null;
	        } 
	        
	        // 如果缓存中有数据
	        if(StringUtils.isNotBlank(skuJson)) {
	            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
	            return pmsSkuInfo;
	        }
	        
	        // 缓存中没有数据
	        // 获取分布式锁key，value
            String uuid = UUID.randomUUID().toString();
            String lockStr = getSkuInfoLockStr(id);
            // 获取分布式锁成功
            if(redisLock.tryLock(lockStr, uuid, Constant.DISTRIBUTED_LOCK_EXPIRE_TIME)) {
                // 查询数据库
                pmsSkuInfo = getFromDB(id);
                
                // 存入缓存
                if(pmsSkuInfo != null) {
                    jedis.set(skuKey, JSON.toJSONString(pmsSkuInfo));   
                } else { 
                    // 避免缓存穿透，设置空串给查询的键
                    jedis.setex(skuKey, 60 * 3, JSON.toJSONString(""));
                }
                // 释放锁
                redisLock.unLock(lockStr, uuid);
                return pmsSkuInfo;
            }
		} finally {
		    CloseUtil.close(jedis);
		}

        // 获取分布式锁失败，自旋几秒，之后访问本地方法，继续查询
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            LOG.error("根据ID查询sku信息，自旋中断", e);
        }
        return get(id);
	}

	/**
	 * 从数据库中查询
	 * @param id
	 * @return
	 */
	private PmsSkuInfo getFromDB(String id) {
		// 商品信息
		PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(id);
		if (pmsSkuInfo == null) {
			return pmsSkuInfo;
		}

		// 图片信息
		PmsSkuImage pmsSkuImage = new PmsSkuImage();
		pmsSkuImage.setSkuId(id);
		List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
		if (pmsSkuImages != null && !pmsSkuImages.isEmpty()) {
			pmsSkuInfo.setSkuImageList(pmsSkuImages);
		}

		return pmsSkuInfo;
	}

	@Override
	public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
		return pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
	}

	@Override
	public List<PmsSkuInfo> getAllSku() {
		List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
		for(PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
			String skuId = pmsSkuInfo.getId();
			
			PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
			pmsSkuAttrValue.setSkuId(skuId);
			List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
			
			pmsSkuInfo.setSkuAttrValueList(select);
		}
		
		return pmsSkuInfos;
	}

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {
        boolean b = false;

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        BigDecimal price = pmsSkuInfo1.getPrice();

        if (price.compareTo(productPrice) == 0) {
            b = true;
        }

        return b;
    }
}
