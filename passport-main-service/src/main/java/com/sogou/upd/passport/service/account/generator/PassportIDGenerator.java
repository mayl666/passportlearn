package com.sogou.upd.passport.service.account.generator;

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
        if (AccountTypeEnum.isPhone(username, provider)) {
            passportID = username + SEPARATOR_1 + "sohu.com";
        } else if (AccountTypeEnum.isSOHU(provider)) {
            passportID = username;
        } else if (AccountTypeEnum.isConnect(provider)) {
            passportID = username + SEPARATOR_1 + AccountTypeEnum.getProviderStr(provider) + ".sohu.com";
        } else {
            passportID = username;
        }

        return passportID;
    }

    public static PassportIDInfoDO parsePassportId(String passportId) throws ServiceException {
        String[] info = passportId.split(SEPARATOR_1);
        if (info.length != 2) {
            logger.error("[DOException] PassportId Format Error! passportId:{}", passportId);
            throw new ServiceException();
        } else {
            PassportIDInfoDO passportIDInfoDO = new PassportIDInfoDO();
            String firstSegment = info[0];
            String secondSegment = info[1];
            String[] secondSegmentArray = secondSegment.split(SEPARATOR_2);
            if (PhoneUtil.verifyPhoneNumberFormat(firstSegment)) {
                passportIDInfoDO.setUid(firstSegment);
                passportIDInfoDO.setAccountTypeStr(AccountTypeEnum.PHONE.toString());
            } else if (secondSegmentArray.length == 3) {
                passportIDInfoDO.setUid(firstSegment);
                passportIDInfoDO.setAccountTypeStr(secondSegmentArray[0]);
            } else {
                passportIDInfoDO.setUid(passportId);
                passportIDInfoDO.setAccountTypeStr(AccountTypeEnum.EMAIL.toString());
            }
            return passportIDInfoDO;
        }
    }
}
