package com.maj.mall.cart.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.maj.mall.bean.OmsCartItem;
import com.maj.mall.cart.mapper.OmsCartItemMapper;
import com.maj.mall.service.CartService;
import com.maj.mall.util.RedisUtil;

import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

@Service
public class CartServiceImpl implements CartService {

	static final Logger LOG = LoggerFactory.getLogger(CartServiceImpl.class);

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	OmsCartItemMapper omsCartItemMapper;

	@Override
	public OmsCartItem ifCartExistByUser(String memberId, String skuId) {

		OmsCartItem omsCartItem = new OmsCartItem();
		omsCartItem.setMemberId(memberId);
		omsCartItem.setProductSkuId(skuId);
		OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);
		return omsCartItem1;

	}

	@Override
	public void addCart(OmsCartItem omsCartItem) {
		if (StringUtils.isNotBlank(omsCartItem.getMemberId())) {
			omsCartItemMapper.insertSelective(omsCartItem);// 避免添加空值
		} else {
			LOG.error("添加购物车到数据库，用户信息不存在，导致添加失败！");
		}
	}

	@Override
	public void updateCart(OmsCartItem omsCartItemFromDb) {

		Example e = new Example(OmsCartItem.class);
		e.createCriteria().andEqualTo("id", omsCartItemFromDb.getId());

		omsCartItemMapper.updateByExampleSelective(omsCartItemFromDb, e);

	}

	/**
	 * 用户对购物车增删改查，频率很高；更新数据库，之后同步缓存
	 */
	@Override
	public void flushCartCache(String memberId) {

		// 查询用户下的所有购物车商品
		OmsCartItem omsCartItem = new OmsCartItem();
		omsCartItem.setMemberId(memberId);
		List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);

		// 同步到redis缓存中（排序问题，redis顺序和数据库查询出来的顺序肯定不一样）
		Jedis jedis = redisUtil.getJedis();
		try {
			Map<String, String> map = new HashMap<>();
			for (OmsCartItem cartItem : omsCartItems) {
				// 计算一下总价
				cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
				map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
			}
            jedis.del(getUserCartCacheKey(memberId));
            jedis.hmset(getUserCartCacheKey(memberId), map);
		} finally {
			jedis.close();
		}
	}

    /**
     * 获取用户购物车缓存信息key
     * 
     * @param userId
     * @return String
     * @date: 2019年12月5日 下午7:18:16
     */
    private String getUserCartCacheKey(String userId) {
        return "user:" + userId + ":cart";
    }

	@Override
	public List<OmsCartItem> cartList(String userId) {
		Jedis jedis = null;
		List<OmsCartItem> omsCartItems = new ArrayList<>();
		try {
			jedis = redisUtil.getJedis();
			// 查询值
            List<String> hvals = jedis.hvals(getUserCartCacheKey(userId));

			for (String hval : hvals) {
				OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
				omsCartItems.add(omsCartItem);
			}

            omsCartItems.sort(new Comparator<OmsCartItem>() {
                @Override
                public int compare(OmsCartItem cartItem1, OmsCartItem cartItem2) {
                    return cartItem1.getId().compareTo(cartItem2.getId());
                }
            });

		} catch (Exception e) {
			// 处理异常，记录系统日志
			LOG.error("查询购物车列表错误！", e);
			return null;
		} finally {
			jedis.close();
		}

		return omsCartItems;
	}

	@Override
	public void checkCart(OmsCartItem omsCartItem) {

		Example e = new Example(OmsCartItem.class);

		e.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId()).andEqualTo("productSkuId",
				omsCartItem.getProductSkuId());

		omsCartItemMapper.updateByExampleSelective(omsCartItem, e);

		// 缓存同步
		flushCartCache(omsCartItem.getMemberId());

	}
}
