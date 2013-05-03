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

    /**
     * 检查密码格式
     *
     * @param passwd
     * @return
     */
    public static boolean checkPasswd(String passwd) {
        return StringUtils.isAsciiPrintable(passwd) && passwd.length() >= 6 && passwd.length() <= 16;
    }

    public static String constructStateCookieKey(int provider) {
        return AccountTypeEnum.getProviderStr(provider) + "_state";
    }
}
