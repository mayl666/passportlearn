package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 密码生成与验证
 * User: shipengzhi
 * Date: 13-3-26 Time: 下午6:05
 */
public class PwdGenerator {

    private static Logger logger = LoggerFactory.getLogger(PwdGenerator.class);

    // HMAC_SHA1密钥
    public static final
    String HMAC_SHA_KEY = "q2SyvfJ8dTwjK3t0x1pnL78Mrq9FkN5tF00p2wEgQg0HmCFx4GXGONOf5FQykc45Evt8odc9OXjGLNX9KnPNWw==";

    public static final String MD5_SIGN = "$1$";  // md5型salt标识

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

    /**
     * 生成密码
     *
     * @param pwd
     * @param needMD5 明文密码为ture，MD5密码为false
     * @return 返回存储在数据库里的密码
     */
    public static String generatorStoredPwd(String pwd, boolean needMD5) throws Exception {
        try {
            String salt = RandomStringUtils.randomAlphanumeric(8);
            String pwdMD5 = needMD5 ? DigestUtils.md5Hex(pwd.getBytes()) : pwd;
            String cryptPwd = Crypt.crypt(pwdMD5, MD5_SIGN + salt);
            if (cryptPwd.startsWith(MD5_SIGN)) {
                cryptPwd = cryptPwd.substring(3);
            }
            return cryptPwd;
        } catch (Exception e) {
            logger.error("Password generator fail, password:{}", pwd);
            throw e;
        }
    }

    public static boolean verify(String pwd, boolean needMD5, String storedPwd) throws Exception {
        try {
            String actualPwd = MD5_SIGN + storedPwd;
            String pwdMD5 = needMD5 ? DigestUtils.md5Hex(pwd.getBytes()) : pwd;
            return actualPwd.equals(Crypt.crypt(pwdMD5, actualPwd));
        } catch (Exception e) {
            logger.error("Password verify fail, password:" + pwd + ", storedPwd:" + storedPwd);
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        String passwd = "123456";
        String salt = RandomStringUtils.randomAlphanumeric(8);
        String pwd_md5 = DigestUtils.md5Hex(passwd.getBytes());
        String result = Crypt.crypt(pwd_md5, "$1$" + salt);
        if (result.startsWith("$1$")) {
            String storedPwd = result.substring(3);
            System.out.println("[Crypt-result]:" + storedPwd);
        }

        String crypt_result = Crypt.crypt(pwd_md5, result);
        boolean isRight = result.equals(crypt_result);
        System.out.println("[Vertify-result]:" + isRight);
    }

}
