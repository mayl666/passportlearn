package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 密码生成器 User: shipengzhi Date: 13-3-26 Time: 下午6:05
 */
public class PwdGenerator {

    private static Logger logger = LoggerFactory.getLogger(PwdGenerator.class);

    // HMAC_SHA1密钥
    public static final
    String HMAC_SHA_KEY = "q2SyvfJ8dTwjK3t0x1pnL78Mrq9FkN5tF00p2wEgQg0HmCFx4GXGONOf5FQykc45Evt8odc9OXjGLNX9KnPNWw==";

    /**
     * 对明文密码采用HMAC_SHA加密
     */
    public static String generatorPwdSign(String pwd) throws Exception {
        byte[] encryByte;
        try {
            byte[] key = Coder.decryptBASE64(HMAC_SHA_KEY);
            encryByte = Coder.encryptHMAC(pwd, key);
        } catch (Exception e) {
            logger.error("Password encrypt fail, password:{}", pwd);
            throw e;
        }

        return Coder.toHexString(encryByte);
    }
}
