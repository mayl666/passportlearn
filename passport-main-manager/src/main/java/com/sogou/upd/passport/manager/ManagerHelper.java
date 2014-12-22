package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.AccountToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class ManagerHelper {

    private static Logger log = LoggerFactory.getLogger(ManagerHelper.class);


    /**
     * 搜狐域账号校验用户名和密码时添加开关，当搜狐接口异常时，直接切换开关，返回相应的异常提示
     *
     * @return
     */
    public static boolean authUserBySOHUSwitcher() {
        return true;   //表示读SOHU，校验用户名和密码是否匹配
//        return false;  //表示不读搜狐，返回异常提示
    }

    /**
     * 内部接口方法签名生成
     *
     * @param firstStr code算法第一个字符串，可能为userid、mobile、userid+mobile
     * @return
     * @throws Exception
     */
    public static String generatorCode(String firstStr, int clientId, String secret, long ct) {
        //计算默认的code
        String code = "";
        try {
            code = firstStr + clientId + secret + ct;
            code = Coder.encryptMD5(code);
        } catch (Exception e) {
            log.error("calculate default code error", e);
        }
        return code;
    }

    /**
     * 内部接口方法签名生成
     *
     * @param firstStr code算法第一个字符串，可能为userid、mobile、userid+mobile
     * @return
     * @throws Exception
     */
    public static String generatorCodeGBK(String firstStr, int clientId, String secret, long ct) {
        //计算默认的code
        String code = "";
        try {
            code = firstStr + clientId + secret + ct;
            code = Coder.encryptMD5GBK(code);
        } catch (Exception e) {
            log.error("calculate default code error", e);
        }
        return code;
    }

    public static Result setModelForOAuthResult(Result result, String uniqName, AccountToken accountToken, String loginType) throws Exception {
        result.setDefaultModel("accesstoken", accountToken.getAccessToken());
        result.setDefaultModel("refreshtoken", accountToken.getRefreshToken());
        result.setDefaultModel("nick", Coder.encryptBase64(uniqName));
        result.setDefaultModel("passport", Coder.encryptBase64(accountToken.getPassportId()));
        result.setDefaultModel("sid", accountToken.getPassportId());
        result.setDefaultModel("logintype", loginType);
        return result;
    }
}
