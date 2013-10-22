package com.sogou.upd.passport.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {

    private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);

    /**
     * 生成第三方授权中的state参数cookie，防止CRSF攻击
     *
     * @param providerStr
     * @return
     */
    public static String constructStateCookieKey(String providerStr) {
        return providerStr + "_state";
    }

    /**
     * 产品有自定义的第三方appkey
     * 此方法构造自定义产品列表中的存储的key
     *
     * @param clientId
     * @param provider
     * @return
     */
    public static String constructSpecialConnectKey(int clientId, int provider) {
        return clientId + CommonConstant.SEPARATOR_1 + provider;
    }

    /**
     * clientId=1044为浏览器PC端、浏览器移动端、输入法PC端
     * clientId=1105为输入法MAC
     *
     * @param clientId
     * @return
     */
    public static boolean isIePinyinToken(int clientId) {
        return clientId == CommonConstant.PC_CLIENTID || clientId == CommonConstant.PINYIN_MAC_CLIENTID;
    }

    /**
     * clientId=1044为浏览器PC端、浏览器移动端、输入法PC端
     *
     * @param clientId
     * @return
     */
    public static boolean isExplorerToken(int clientId) {
        return clientId == CommonConstant.PC_CLIENTID;
    }

    /**
     * clientId=1105为输入法MAC
     *
     * @param clientId
     * @return
     */
    public static boolean isPinyinMACToken(int clientId) {
        return clientId == CommonConstant.PINYIN_MAC_CLIENTID;
    }

    /*public static void recordTimestamp(long start, String msg) {
        long end = System.currentTimeMillis();
        logger.info(msg + " time:" + (end - start) + "ms");
    } */

}
