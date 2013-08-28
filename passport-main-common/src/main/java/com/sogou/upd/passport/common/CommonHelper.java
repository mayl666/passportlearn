package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {

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
     * @param clientId
     * @param provider
     * @return
     */
    public static String constructSpecialConnectKey(int clientId, int provider) {
        return clientId + CommonConstant.SEPARATOR_1 + provider;
    }
}
