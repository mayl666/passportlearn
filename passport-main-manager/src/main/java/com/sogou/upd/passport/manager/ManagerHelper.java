package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
     * 是否调用代理Api，返回ture调用ProxyXXXApiManager，false调用SGXXXApiManager
     *
     * @param passportId passport内部传输的用户id
     * @return
     */
    public static boolean isInvokeProxyApi(String passportId) {
        return false;
//        return  !AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(passportId));
    }

    /**
     * 是否需要双读，即先读SG，再读SH，返回true表示需要双读，返回false表示不需要双读
     *
     * @return
     */
    public static boolean isBothReadApi(String passportId) {
        return true;//todo 第一次双读上线的返回结果，恒为true，双读SG,SH
//        return  !AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(passportId));todo 第二次双读上线（也即搜狗账号第二次写分离上线）打开此开关，搜狗账号恒为false，不双读，只读SG；其它账号恒为true，双读SG,SH
//        return false;                                                                           todo 其它账号双读上线（也其它账号第二次写分离上线）打开此开关，恒为false，所有账号不双读，只读SG；
    }

    /**
     * 是否需要只读SG库。当isBothReadApi方法返回false时：此方法返回true表示只读SG库；返回false表示只读SH线上，相当于回滚操作
     *
     * @return
     */
    public static boolean readSogouSwitcher() {
        return true; //todo 正常线上都应该恒为true
//        return false;todo 若非上线后出故障，回滚至SOHU代码，打开此开关，即为回滚，前提：isBothReadApi必须为false

    }


    /**
     * 是否使用sohu提供的getcookiinfo接口；返回true代表调用getcookieinfo接口，false代表调用之前的从location拿的接口，为回滚做准备
     *
     * @return
     */
    public static boolean isUsedSohuProxyApiToGetCookie() {
        return true;
//        return false;
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
