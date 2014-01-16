package com.sogou.upd.passport.manager.api.connect.impl.user;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:21
 * To change this template use File | Settings | File Templates.
 */
@Component("sgUserOpenApiManager")
public class SGUserOpenApiManagerImpl implements UserOpenApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGUserOpenApiManagerImpl.class);

    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String userid = userOpenApiParams.getUserid();
            ConnectUserInfoVO cacheConnectUserInfoVO = connectAuthService.obtainConnectUserInfo(userid);
            if(cacheConnectUserInfoVO != null){
                result = buildSuccResult(cacheConnectUserInfoVO, userid);
                return result;
            }

            int clientId = userOpenApiParams.getClient_id();
            //获取第三方信息
            String providerStr = getProviderByUserid(userid);
            if (StringUtils.isBlank(providerStr)) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_USERID_TYPE_ERROR);
                return result;
            }
            int provider = AccountTypeEnum.getProvider(providerStr);
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            String openId = null;
            String accessToken = null;
            //去sohu获取token
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setOpenid(userid);
            baseOpenApiParams.setUserid(userid);
            Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
                openId = accessTokenMap.get("open_id").toString();
                accessToken = accessTokenMap.get("access_token").toString();
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_USERID_TYPE_ERROR);
                return result;
            }
            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
            if (connectUserInfoVO == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                return result;
            }
            result = buildSuccResult(connectUserInfoVO, userid);
            connectAuthService.initialOrUpdateConnectUserInfo(userid,cacheConnectUserInfoVO);
            return result;
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
        } catch (OAuthProblemException ope) {
            logger.error("handle oauth authroize code error!", ope);
            result = buildErrorResult(ope.getError(), ope.getDescription());
        } catch (Exception exp) {
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!");
        }
        return result;

    }

    private String getProviderByUserid(String userid) {
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userid);
        if (domain != AccountDomainEnum.THIRD) {
            return "";
        }
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(userid);
        if (accountTypeEnum == AccountTypeEnum.UNKNOWN) {
            return "";
        }
        return accountTypeEnum.toString();
    }

    private Result buildErrorResult(String errorCode, String errorText) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        return result;
    }

    private Result buildSuccResult(ConnectUserInfoVO connectUserInfoVO, String userid) {
        Result userInfoResult = new APIResultSupport(true);
        Map<String, Object> data = Maps.newHashMap();
        Map<String, Object> result_value_data = Maps.newHashMap();
        result_value_data.put("id", "");
        result_value_data.put("birthday", "");
        result_value_data.put("sex", connectUserInfoVO.getGender());
        result_value_data.put("nick", connectUserInfoVO.getNickname());
        result_value_data.put("location", connectUserInfoVO.getProvince() + " " + connectUserInfoVO.getCity() + " " + connectUserInfoVO.getRegion());
        result_value_data.put("headurl", connectUserInfoVO.getImageURL());
        data.put("result", result_value_data);
        data.put("original", connectUserInfoVO.getOriginal());
        data.put("userid", userid);
        data.put("openid", userid);
        userInfoResult.setModels(data);
        userInfoResult.setMessage("操作成功");
        return userInfoResult;
    }

}