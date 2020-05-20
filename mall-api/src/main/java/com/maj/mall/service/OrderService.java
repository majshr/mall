package com.maj.mall.service;

import com.maj.mall.bean.OmsOrder;

public interface OrderService {

    /**
     * 检查订单对应的code（验证是否可用码）是否存在
     * 
     * @param memberId
     * @param tradeCode
     * @return boolean
     * @date: 2019年12月17日 下午4:46:34
     */
    boolean checkTradeCode(String memberId, String tradeCode);

    /**
     * 生成订单码
     * 
     * @param memberId
     * @return String
     * @date: 2019年12月17日 下午4:47:14
     */
    String genTradeCode(String memberId);

    /**
     * 保存订单
     * 
     * @param omsOrder
     * @date: 2019年12月17日 下午4:47:30
     */
    void saveOrder(OmsOrder omsOrder);

    /**
     * 根据订单编号获取订单
     * 
     * @param outTradeNo
     * @return OmsOrder
     * @date: 2019年12月19日 下午5:31:18
     */
    OmsOrder getOrderByOutTradeNo(String outTradeNo);
}
