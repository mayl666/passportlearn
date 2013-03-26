package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.CommonParameters;

/**
 * 密码生成器
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class PwdGenerator {

    public String generatorPwdSign(String pwd) throws SystemException {
        byte[] encryByte;
        try {
            encryByte = Coder.encryptHMAC(pwd.toString().getBytes(), CommonParameters.HMAC_SHA_KEY);
        } catch (Exception e) {
            // todo record error log
            throw new SystemException(e);
        }

        return Coder.toHexString(encryByte);
    }
}
