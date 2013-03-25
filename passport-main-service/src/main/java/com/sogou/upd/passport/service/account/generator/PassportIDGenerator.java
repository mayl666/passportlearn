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
     * @param account
     * @param provider
     * @return
     */
    public static String generator(String account, int provider) {
        String passportID = null;
        if (AccountTypeEnum.isPhone(account, provider)) {
            passportID = account + "@sohu.com";
        } else if (AccountTypeEnum.isConnect(provider)) {
            passportID = account + "@" + AccountTypeEnum.getProviderStr(provider) + ".sohu.com";
        } else {
            passportID = account;
        }

        return passportID;
    }
}
