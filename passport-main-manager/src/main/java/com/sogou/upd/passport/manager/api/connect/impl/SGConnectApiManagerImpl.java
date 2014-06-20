package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectRequest;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountService;
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
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        OAuthConsumer oAuthConsumer;
        OAuthAuthzClientRequest request;
        ConnectConfig connectConfig;
        try {
            int clientId = Integer.parseInt(connectLoginParams.getClient_id());
            oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            // 获取connect配置
            connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
            }

            String redirectURI = ConnectManagerHelper.constructRedirectURI(clientId, connectLoginParams.getRu(), connectLoginParams.getType(),
                    connectLoginParams.getTs(), oAuthConsumer.getCallbackUrl(httpOrHttps), ip, connectLoginParams.getFrom(), connectLoginParams.getDomain(), connectLoginParams.getThirdInfo());
            String scope = connectConfig.getScope();
            String appKey = connectConfig.getAppKey();
            String connectType = connectLoginParams.getType();
            // 重新填充display，如果display为空，根据终端自动赋值；如果display不为空，则使用display
            String display = connectLoginParams.getDisplay();
            display = Strings.isNullOrEmpty(display) ? fillDisplay(connectType, connectLoginParams.getFrom(), provider) : display;

            String requestUrl;
            // 采用Authorization Code Flow流程
            //若provider=QQ && display=wml、xhtml调用WAP接口
            if (ConnectRequest.isQQWapRequest(connectLoginParams.getProvider(), display)) {
                requestUrl = oAuthConsumer.getWapUserAuthzUrl();
            } else {
                requestUrl = oAuthConsumer.getWebUserAuthzUrl();
            }
            OAuthAuthzClientRequest.AuthenticationRequestBuilder builder = OAuthAuthzClientRequest
                    .authorizationLocation(requestUrl).setAppKey(appKey)
                    .setRedirectURI(redirectURI)
                    .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                    .setDisplay(display, provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(uuid);
            if (AccountTypeEnum.QQ.getValue() == provider) {
                builder.setShowAuthItems(QQOAuth.NO_AUTH_ITEMS);       // qq为搜狗产品定制化页面，隐藏授权信息
                if (!Strings.isNullOrEmpty(connectLoginParams.getViewPage())) {
                    builder.setViewPage(connectLoginParams.getViewPage());       // qq为搜狗产品定制化页面--输入法使用
                }
            }
            request = builder.buildQueryMessage(OAuthAuthzClientRequest.class);
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException!");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception!");
        }

        return request.getLocationUri();
    }

    @Override
    public Result buildConnectAccount(String appKey, int provider, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = AccountTypeEnum.generateThirdPassportId(oAuthTokenVO.getOpenid(), AccountTypeEnum.getProviderStr(provider));
            //1.查询account表
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                account = accountService.initialAccount(oAuthTokenVO.getOpenid(), null, false, oAuthTokenVO.getIp(), provider);
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
            ConnectRelation connectRelation = connectRelationService.querySpecifyConnectRelation(oAuthTokenVO.getOpenid(), provider, appKey);
            if (connectRelation == null) {
                connectRelation = newConnectRelation(appKey, passportId, oAuthTokenVO.getOpenid(), provider);
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
    public Result obtainConnectToken(String passportId, int clientId) throws ServiceException {
        Result result = new APIResultSupport(false);
        try {
            int provider = AccountTypeEnum.getAccountType(passportId).getValue();
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            ConnectToken connectToken;
            if (connectConfig != null) {
                connectToken = connectTokenService.queryConnectToken(passportId, provider, connectConfig.getAppKey());
                if (connectToken == null || !verifyAccessToken(connectToken, connectConfig)) {           //判断accessToken是否过期，是否需要刷新
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_ACCESSTOKEN_NOT_FOUND);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_CLIENTID_PROVIDER_NOT_FOUND);
                return result;
            }
            result.setSuccess(true);
            result.setDefaultModel("connectToken", connectToken);
        } catch (Exception e) {
//            logger.error("method[obtainConnectToken] obtain connect token from sogou db error passportId:{}", passportId, e);
            logger.error("obtain connect token from sogou db error.passportId [{}] clientId {}", passportId, clientId, e);
        }
        return result;
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
            connectToken.setConnectUniqname(StringUtil.filterConnectUniqname(connectUserInfoVO.getNickname()));
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
                OAuthTokenVO oAuthTokenVO = connectAuthService.refreshAccessToken(refreshToken, connectConfig);
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

    /*
     * 根据type和provider重新填充display
     */
    private String fillDisplay(String type, String from, int provider) {
        String display = "";
        if (ConnectTypeEnum.isMobileApp(type) || isMobileDisplay(type, from)) {
            switch (provider) {
                case 5:  // 人人
                    display = "touch";
                    break;
                case 6:  // 淘宝
                    display = "wap";
                    break;
                default:
                    display = "mobile";
                    break;
            }
        }
        return display;
    }

    private boolean isMobileDisplay(String type, String from) {
        return type.equals(ConnectTypeEnum.TOKEN.toString()) && "mob".equalsIgnoreCase(from)
                || type.equals(ConnectTypeEnum.MOBILE.toString());
    }

}
