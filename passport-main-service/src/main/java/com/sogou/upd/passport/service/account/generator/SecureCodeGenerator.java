package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-21 Time: 上午11:10 To change this template
 * use File | Settings | File Templates.
 */
public class SecureCodeGenerator {

    private static Logger logger = LoggerFactory.getLogger(SecureCodeGenerator.class);

    /**
     * 安全码，作为前一步（上一页面）已验证标志传递到下一步（下一页面）
     *
     * @param passportId
     * @param clientId
     * @return
     * @throws Exception
     */
    public static String generatorSecureCode(String passportId, int clientId) throws Exception {
        String secureCode = null;
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            secureCode = Coder.encryptMD5(passportId + "_" + clientId + "_" + code);
        } catch (Exception e) {
            logger.error("Check Code Generator Fail, passportId: " + passportId + ", clientId: " +
                clientId);
            throw e;
        }
        return secureCode;
    }
}
