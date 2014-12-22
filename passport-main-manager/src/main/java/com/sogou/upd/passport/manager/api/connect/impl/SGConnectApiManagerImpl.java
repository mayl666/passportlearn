package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.sogou.upd.passport.common.CommonConstant.SEPARATOR_1;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:22
 * To change this template use File | Settings | File Templates.
 */
@Component("sgConnectApiManager")
public class SGConnectApiManagerImpl implements ConnectApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGConnectApiManagerImpl.class);
    public static final String TKEY_VERSION = "01";
    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ConnectRelationService connectRelationService;
    @Autowired
    private ConnectAuthService connectAuthService;

    @Override
    public Result buildConnectAccount(String appKey, int provider, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        try {
            String openId = oAuthTokenVO.getOpenid(); // 第三方返回的openid，并不一定是用户的唯一标识(例如：微信）
            String unionId = openId;   // 用户的唯一标识
            //由于微信一个开发者账号下多个Appid的unionid一样，所以微信是unionid
            if (provider == AccountTypeEnum.WEIXIN.getValue()) {
                unionId = oAuthTokenVO.getUnionId();
                if (Strings.isNullOrEmpty(unionId)) {
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_WEIXIN_UNIONID);
                    return result;
                }
            }
            String passportId = PassportIDGenerator.generator(unionId, provider);
            //1.查询account表
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                account = accountService.initialAccount(unionId, null, false, oAuthTokenVO.getIp(), provider);
                if (account == null) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    return result;
                }
            }
            boolean isSuccess;
            //2.connect_token表新增或修改
            ConnectToken connectToken = newConnectToken(passportId, appKey, provider, oAuthTokenVO);
            isSuccess = connectTokenService.insertOrUpdateConnectToken(connectToken);
            if (!isSuccess) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                return result;
            }
            //3.connect_relation新增或修改
            ConnectRelation connectRelation = connectRelationService.querySpecifyConnectRelation(openId, provider, appKey);
            if (connectRelation == null) {
                connectRelation = newConnectRelation(appKey, passportId, openId, provider);
                isSuccess = connectRelationService.initialConnectRelation(connectRelation);
            }
            if (!isSuccess) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                return result;
            }
            // type=pc时需要昵称字段
            String uniqName = account.getUniqname();
            if (Strings.isNullOrEmpty(uniqName)) {
                uniqName = connectToken.getConnectUniqname();
            }
            result.setSuccess(true);
            result.setDefaultModel("connectToken", connectToken);
            result.setDefaultModel("uniqName", uniqName);
        } catch (ServiceException se) {
            logger.error("[connect]method buildConnectAccount ServiceException: database operation error.{}", se);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (Exception e) {
            logger.error("[connect] method buildConnectAccount error.{}", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result obtainConnectToken(String passportId, int clientId, String thirdAppId) throws ServiceException {
        Result result = new APIResultSupport(false);
        try {
            int provider = AccountTypeEnum.getAccountType(passportId).getValue();
            ConnectConfig connectConfig = connectConfigService.queryConnectConfigByAppId(thirdAppId, provider);
            ConnectToken connectToken;
            if (connectConfig != null) {
                connectToken = connectTokenService.queryConnectToken(passportId, provider, connectConfig.getAppKey());
                if (connectToken == null || !verifyAccessToken(connectToken, connectConfig)) {           //判断accessToken是否过期，是否需要刷新
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_ACCESSTOKEN_NOT_FOUND);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                return result;
            }
            result.setSuccess(true);
            result.setDefaultModel("connectToken", connectToken);
        } catch (Exception e) {
            logger.error("obtain connect token from sogou db error.passportId [{}] clientId {}", passportId, clientId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result obtainTKey(String passportId, int clientId) {
        Result result = new APIResultSupport(false);
        Result connectTokenResult = obtainConnectToken(passportId, clientId, null);
        if (!connectTokenResult.isSuccess()) {
            return connectTokenResult;
        }
        ConnectToken connectToken = (ConnectToken) connectTokenResult.getModels().get("connectToken");
        try {
            String tKey = String.format("%s|%s|%s|%s|%s|%s|%s", connectToken.getOpenid(), connectToken.getAccessToken(), connectToken.getExpiresIn(), connectToken.getAppKey(), connectToken.getPassportId(), clientId, System.currentTimeMillis());
            tKey = TKEY_VERSION + SEPARATOR_1 + AES.encryptURLSafeString(tKey, TKEY_SECURE_KEY);
            result.setSuccess(true);
            result.getModels().put("tKey", tKey);
            return result;
        } catch (Exception e) {
            logger.error("obtain tKey AES fail,passportId:{}", passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    private ConnectToken newConnectToken(String passportId, String appKey, int provider, OAuthTokenVO oAuthTokenVO) {
        ConnectToken connectToken = new ConnectToken();
        connectToken.setPassportId(passportId);
        connectToken.setAppKey(appKey);
        connectToken.setProvider(provider);
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getOpenid())) {
            connectToken.setOpenid(oAuthTokenVO.getOpenid());
        }
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getAccessToken())) {
            connectToken.setAccessToken(oAuthTokenVO.getAccessToken());
        }
        if (oAuthTokenVO.getExpiresIn() > 0) {
            connectToken.setExpiresIn(oAuthTokenVO.getExpiresIn());
        }
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getRefreshToken())) {
            connectToken.setRefreshToken(oAuthTokenVO.getRefreshToken());
        }
        connectToken.setUpdateTime(new Date());
        ConnectUserInfoVO connectUserInfoVO = oAuthTokenVO.getConnectUserInfoVO();
        if (connectUserInfoVO != null) {
            connectToken.setConnectUniqname(connectUserInfoVO.getNickname());
            connectToken.setGender(String.valueOf(connectUserInfoVO.getGender()));
            connectToken.setAvatarSmall(connectUserInfoVO.getAvatarSmall());
            connectToken.setAvatarMiddle(connectUserInfoVO.getAvatarMiddle());
            connectToken.setAvatarLarge(connectUserInfoVO.getAvatarLarge());
        }
        return connectToken;
    }

    protected ConnectRelation newConnectRelation(String appKey, String passportId, String openId, int provider) {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setAppKey(appKey);
        connectRelation.setOpenid(openId);
        connectRelation.setPassportId(passportId);
        connectRelation.setProvider(provider);
        return connectRelation;
    }

    /**
     * 根据refreshToken是否过期，来决定是否用refreshToken来刷新accessToken
     *
     * @param connectToken
     * @param connectConfig
     * @return
     * @throws IOException
     * @throws OAuthProblemException
     */
    private boolean verifyAccessToken(ConnectToken connectToken, ConnectConfig connectConfig) throws IOException, OAuthProblemException {
        if (!isValidToken(connectToken.getUpdateTime(), connectToken.getExpiresIn())) {
            String refreshToken = connectToken.getRefreshToken();
            //refreshToken不为空，则刷新token
            if (!Strings.isNullOrEmpty(refreshToken)) {
                OAuthTokenVO oAuthTokenVO = null;
                try {
                    oAuthTokenVO = connectAuthService.refreshAccessToken(refreshToken, connectConfig);
                } catch (OAuthProblemException e) {
                    logger.warn("Refresh connect refreshtoken fail, errorCode:" + e.getError() + " ,errorDesc:" + e.getDescription());
                }
                if (oAuthTokenVO == null) {
                    return false;
                }
                //如果SG库中有token信息，但是过期了，此时使用refreshToken刷新成功了，这时要双写搜狗、搜狐数据库
                connectToken.setAccessToken(oAuthTokenVO.getAccessToken());
                connectToken.setExpiresIn(oAuthTokenVO.getExpiresIn());
                connectToken.setRefreshToken(oAuthTokenVO.getRefreshToken());
                connectToken.setUpdateTime(new Date());
                boolean isUpdateSuccess = connectTokenService.insertOrUpdateConnectToken(connectToken);
                return isUpdateSuccess;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证Token是否失效,返回true表示有效，false表示过期
     */
    private boolean isValidToken(Date createTime, long expiresIn) {
        long currentTime = System.currentTimeMillis() / (1000);
        long tokenTime = createTime.getTime() / (1000);
        return currentTime < tokenTime + expiresIn;
    }

}
