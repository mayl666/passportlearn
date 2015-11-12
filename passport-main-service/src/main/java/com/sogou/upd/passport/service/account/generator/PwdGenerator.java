package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.SohuPasswordType;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

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
     * 生成密码
     *
     * @param pwd     密码
     * @param needMD5 明文密码为ture，MD5密码为false
     * @return 返回存储在数据库里的密码
     */
    public static String generatorStoredPwd(String pwd, boolean needMD5) throws Exception {
        try {
            String salt = RandomStringUtils.randomAlphanumeric(8);
            if (Strings.isNullOrEmpty(pwd)) {
                return "";
            }
            String pwdMD5 = needMD5 ? DigestUtils.md5Hex(pwd.getBytes()) : pwd;
            String cryptPwd = Crypt.crypt(pwdMD5.toLowerCase(), MD5_SIGN + salt);
            if (cryptPwd.startsWith(MD5_SIGN)) {
                cryptPwd = cryptPwd.substring(3);
            }
            return cryptPwd;
        } catch (Exception e) {
            logger.error("Password generator fail, password:{}", pwd);
            throw e;
        }
    }

    /**
     * sohu导入账号，用户修改密码时，存password的md5
     * @param pwd sohu password plain
     * @return 存在数据库中的sohu账号密码的md5
     * @throws Exception
     */
    public static String generatorSohuPwd(String pwd) throws Exception{
        try{
            String sohuMd5Pwd=DigestUtils.md5Hex(pwd.getBytes());
            return sohuMd5Pwd;

        }catch (Exception e){
            logger.error("sohu password generator faail,password:{}",pwd);
            throw e;
        }

    }

    /**
     * 校验密码正确性
     *
     * @param pwd       密码
     * @param needMD5   密码是否需要MD5，明文密码为ture，MD5密码为false
     * @param storedPwd 数据库里存储的密码
     * @return 如果前端传递的密码与数据库里密码匹配，返回true
     * @throws Exception
     */
    public static boolean verify(String pwd, boolean needMD5, String storedPwd) throws Exception {
        try {
            String actualPwd = MD5_SIGN + storedPwd;
            String pwdMD5 = needMD5 ? DigestUtils.md5Hex(pwd.getBytes()) : pwd;
            String validPwd = Crypt.crypt(pwdMD5, actualPwd);
            return actualPwd.equals(validPwd);
        } catch (Exception e) {
            logger.error("Password verify fail, password:" + pwd + ", storedPwd:" + storedPwd);
            throw e;
        }
    }


    /**
     * 按照sohu算法校验密码正确性
     * @param storedPwd
     * @param pwd
     * @param pwdType
     * @return
     * @throws Exception
     */
    public static boolean verifySohuPwd(String storedPwd,  String pwd, SohuPasswordType pwdType)throws Exception{
        if (Strings.isNullOrEmpty(storedPwd) || Strings.isNullOrEmpty(pwd) ) {
            return false;
        }
        String salt = "";
        if (storedPwd.contains("$")) {
            String[] passwordArray = storedPwd.split("\\$");
            salt = passwordArray[0];
        }
        if (pwdType == SohuPasswordType.TEXT) {
            try {
                return storedPwd.equals(Crypt.crypt(Coder.encryptMD5(pwd).getBytes("UTF-8"), MD5_SIGN + salt).substring(3));
            } catch (UnsupportedEncodingException e) {
                logger.error("Sohu Password verify fail, password:" + pwd + ", storedPwd:" + storedPwd);
                return false;
            }
        } else if (pwdType == SohuPasswordType.MD5) {
            try {
                return storedPwd.equals(Crypt.crypt(pwd.getBytes("UTF-8"), MD5_SIGN + salt).substring(3));
            } catch (UnsupportedEncodingException e) {
                logger.error("Sohu Password verify fail, password:" + pwd + ", storedPwd:" + storedPwd);
                return false;
            }
        }
        return false;

    }


    public static void main(String[] args) throws Exception {
//        String passwd = "123456";
//        String salt = RandomStringUtils.randomAlphanumeric(8);
//        String pwd_md5 = DigestUtils.md5Hex(passwd.getBytes());
//        String result = Crypt.crypt(pwd_md5, "$1$" + salt);
//        if (result.startsWith("$1$")) {
//            String storedPwd = result.substring(3);
//            System.out.println("[Crypt-result]:" + storedPwd);
//        }
//
//        String crypt_result = Crypt.crypt(pwd_md5, result);
//        boolean isRight = result.equals(crypt_result);
//        System.out.println("[Vertify-result]:" + isRight);
        System.out.println(PwdGenerator.generatorStoredPwd("111111", true));
    }

}
