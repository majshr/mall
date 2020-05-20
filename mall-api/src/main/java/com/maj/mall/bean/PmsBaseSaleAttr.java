package com.maj.mall.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 基础销售属性信息，用户在添加商品销售属性时，从这里选择属性信息，然后自己设置值
 * @param
 * @return
 */
public class PmsBaseSaleAttr implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}