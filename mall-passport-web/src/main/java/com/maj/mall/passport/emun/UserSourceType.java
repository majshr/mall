package com.maj.mall.passport.emun;

/**
 * 用户来源枚举
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年12月16日 下午2:52:29
 */
public enum UserSourceType {
    local(1), vb(2);
    private int sourceType;

    UserSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 获取来源
     * 
     * @return int
     * @date: 2019年12月16日 下午2:54:06
     */
    public int getSourceType() {
        return sourceType;
    }
}
