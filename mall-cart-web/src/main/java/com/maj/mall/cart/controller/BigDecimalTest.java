package com.maj.mall.cart.controller;

import java.math.BigDecimal;

public class BigDecimalTest {
    public static void main(String[] args) {
    	//**********************初始化使用字符串，可以保证没问题*****************************
        // 初始化
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");
        // 0.00999999977648258209228515625
        System.out.println(b1);
        // 0.01000000000000000020816681711721685132943093776702880859375
        System.out.println(b2);
        // 0.01
        System.out.println(b3);

        // 比较
        int i = b1.compareTo(b2);// 1 0 -1
        // -1
        System.out.println(i);

        // 运算
        // 加法
        BigDecimal add = b1.add(b2);
        // 0.01999999977648258230045197336721685132943093776702880859375
        System.out.println(add);

        // 减法
        BigDecimal subtract = b2.subtract(b1);
        // 科学计数法
        // 2.2351741811588166086721685132943093776702880859375E-10    
        System.out.println(subtract);

        BigDecimal b4 = new BigDecimal("6");
        BigDecimal b5 = new BigDecimal("7");
        // 乘法    42
        BigDecimal multiply = b4.multiply(b5);
        System.out.println(multiply);

        //除法	0.857
        // 3位小数，取舍规则：四舍五入
        BigDecimal divide = b4.divide(b5, 3, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(divide);

        // 约数
        BigDecimal subtract1 = b2.add(b1);
        // 手动设置取舍规则
        BigDecimal bigDecimal = subtract1.setScale(3, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(bigDecimal);

    }
}
