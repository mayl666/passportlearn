package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

/**
 * passportID生成算法
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:53
 * To change this template use File | Settings | File Templates.
 */
public class PassportIDGenerator {

    /**
     * 根据account生成passportid
     * @param username
     * @param provider
     * @return
     */
    public static String generator(String username, int provider) {
        String passportID;
        if (AccountTypeEnum.isPhone(username, provider)) {
            passportID = username + "@sohu.com";
        } else if (AccountTypeEnum.isConnect(provider)) {
            passportID = username + "@" + AccountTypeEnum.getProviderStr(provider) + ".sohu.com";
        } else {
            passportID = username;
        }

        return passportID;
    }
}
