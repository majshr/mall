package com.maj.mall.service;

import com.maj.mall.bean.PaymentInfo;

public interface PaymentService {

    /**
     * 保存支付信息到数据库
     * 
     * @param paymentInfo
     * @date: 2019年12月19日 上午11:32:24
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新支付信息
     * 
     * @param paymentInfo
     * @date: 2019年12月19日 上午11:33:21
     */
    void updatePayment(PaymentInfo paymentInfo);
}
