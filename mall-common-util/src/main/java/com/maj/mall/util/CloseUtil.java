package com.maj.mall.util;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseUtil {

    static Logger LOG = LoggerFactory.getLogger(CloseUtil.class);

    /**
     * 关闭资源
     * 
     * @param source
     * @return boolean
     * @date: 2019年11月29日 上午11:12:40
     */
    public static boolean close(Closeable source) {

        if (source != null) {
            try {
                source.close();
            } catch (IOException e) {
                LOG.error("关闭资源错误！", e);
                return false;
            }
        }
        return true;
    }
}
