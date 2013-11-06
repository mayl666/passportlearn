package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
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
     * 创建一个第三方账户对象
     */
    public static ConnectToken buildConnectToken(String passportId, int provider, String appKey, String openid, String accessToken, long expiresIn, String refreshToken) {
        ConnectToken connect = new ConnectToken();
        connect.setPassportId(passportId);
        connect.setProvider(provider);
        connect.setAppKey(appKey);
        connect.setOpenid(openid);
        connect.setAccessToken(accessToken);
        connect.setExpiresIn(expiresIn);
        connect.setRefreshToken(refreshToken);
        connect.setCreateTime(new Date());
        return connect;
    }

    /**
     * 创建一个第三方关系关系（反查表）对象
     */
    public static ConnectRelation buildConnectRelation(String openid, int provider, String passportId, String appKey) {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setOpenid(openid);
        connectRelation.setProvider(provider);
        connectRelation.setPassportId(passportId);
        connectRelation.setAppKey(appKey);
        return connectRelation;
    }

    /**
     * 是否调用代理Api，返回ture调用ProxyXXXApiManager，false调用SGXXXApiManager
     *
     * @param passportId passport内部传输的用户id
     * @return
     */
    public static boolean isInvokeProxyApi(String passportId) {
        return true;//
//        return  !AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(passportId));
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
}
