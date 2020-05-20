package com.maj.mall.service;

import java.util.List;

import com.maj.mall.bean.OmsCartItem;

public interface CartService {
	/**
     * query用户对指定商品是否有订单
     * 
     * @param memberId
     *            用户
     * @param skuId
     *            商品ID
     * @return
     */
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    /**
     * 添加用户订单到数据库
     * @param omsCartItem
     */
    void addCart(OmsCartItem omsCartItem);

    /**
     * 更新用户订单
     * @param omsCartItemFromDb
     */
    void updateCart(OmsCartItem omsCartItemFromDb);

    /**
     * 刷新用户订单缓存信息
     * 
     * @param memberId
     */
    void flushCartCache(String memberId);

    /**
     * 用户订单列表（redis查询）
     * @param userId
     * @return
     */
    List<OmsCartItem> cartList(String userId);

    /**
     * 更新用户订单选中，未选中状态
     * 
     * @param omsCartItem
     */
    void checkCart(OmsCartItem omsCartItem);
}
