package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi                                                   x
 * Date: 13-5-6
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public class SecureSignatureGenerator {

    private static Logger logger = LoggerFactory.getLogger(SecureSignatureGenerator.class);

    public static String sign(SecureSignDO secureSignatureDO, String secret) throws Exception {
        StringBuilder baseBuilderString = new StringBuilder("");
        baseBuilderString.append(secureSignatureDO.getTs()).append("\n");
        baseBuilderString.append(secureSignatureDO.getNonce()).append("\n");
        baseBuilderString.append(secureSignatureDO.getUri()).append("\n");
        baseBuilderString.append(secureSignatureDO.getServerName()).append("\n");
        String baseString = baseBuilderString.toString();
        try {
            String signature = Coder.encryptBASE64(Coder.encryptHMAC(baseString, secret));
            return signature;
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

    public static boolean verify(SecureSignDO secureSignatureDO, String secret, String signature) throws Exception {
        try {
            String actual = sign(secureSignatureDO, secret);
            return actual.equals(signature);
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

}
