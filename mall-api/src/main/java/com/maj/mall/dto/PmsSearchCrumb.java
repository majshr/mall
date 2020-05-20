package com.maj.mall.dto;

/**
 * 面包屑参数（选中的基本属性）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年12月4日 下午2:31:13
 */
public class PmsSearchCrumb {
    private String valueId;
    private String valueName;
    private String urlParam;

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

}
