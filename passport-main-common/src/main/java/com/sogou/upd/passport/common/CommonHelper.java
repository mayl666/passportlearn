package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {

    public static String constructStateCookieKey(int provider) {
        return AccountTypeEnum.getProviderStr(provider) + "_state";
    }
}
