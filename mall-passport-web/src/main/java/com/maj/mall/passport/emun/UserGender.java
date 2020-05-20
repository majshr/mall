package com.maj.mall.passport.emun;

/**
 * 用户性别枚举
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年12月16日 下午3:02:42
 */
public enum UserGender {

    man(1), woman(0);

    private int type;

    private UserGender(int type) {
        this.type = type;
    }

    /**
     * 获取用户类型
     */
    public int getType() {
        return type;
    }
}
