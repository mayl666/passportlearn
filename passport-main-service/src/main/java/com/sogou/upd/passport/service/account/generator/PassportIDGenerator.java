package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.dataobject.PassportIDInfoDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * passportID生成算法
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:53
 * To change this template use File | Settings | File Templates.
 */
public class PassportIDGenerator {

    private static final String SEPARATOR_1 = "@";
    private static final String SEPARATOR_2 = ".";

    private static Logger logger = LoggerFactory.getLogger(PassportIDGenerator.class);

    /**
     * 根据account生成passportid
     *
     * @param username
     * @param provider
     * @return
     */
    public static String generator(String username, int provider) {
        String passportID;
        if (AccountTypeEnum.isPhone(username, provider)) {  //手机号
            passportID = username + SEPARATOR_1 + "sohu.com";
        } else if (AccountTypeEnum.isConnect(provider)) {  //第三方 由于微信一个开发者账号下多个Appid的unionid一样，所以微信是unionid；其他第三方均为openid
            passportID = username + SEPARATOR_1 + AccountTypeEnum.getProviderStr(provider) + ".sohu.com";
        } else if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(username))) { //个性账号
            passportID = username + SEPARATOR_1 + "sogou.com";
        } else {
            passportID = username;
        }

        return passportID;
    }
}
