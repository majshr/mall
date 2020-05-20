package com.maj.mall.dto;

import java.io.Serializable;
/**
 * 主页面查询参数类
 * @author maj
 *
 */
public class PmsSearchParam implements Serializable{

    private static final long serialVersionUID = -5460263658824021812L;

    private String catalog3Id;

    private String keyword;

    /**
     * 属性值集合
     */
    private String[] valueId;
    
    private Integer from;
    
    private Integer size;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    
    
}
