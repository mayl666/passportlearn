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
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        //todo 是否考虑刚切换时，拿到的sohu token，取不到userinfo
        Result result = new APIResultSupport(false);
        try {
            String userid = userOpenApiParams.getUserid();
            int clientId = userOpenApiParams.getClient_id();

            //查询用户存储在sogou passport的用户信息
            ObtainAccountInfoParams getUserInfoparams = new ObtainAccountInfoParams();
            getUserInfoparams.setUsername(userid);
            getUserInfoparams.setClient_id(String.valueOf(clientId));
            getUserInfoparams.setFields("uniqname,birthday,gender,province,city,avatarurl");

            Result getUserInfoResult = accountInfoManager.getUserInfo(getUserInfoparams);
            if (!getUserInfoResult.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_GET_USER_INFO);
                return result;
            }

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
            Result openResult = sgConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
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
            result = buildSuccResult(getUserInfoResult, connectUserInfoVO, userid);
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

    private Result buildSuccResult(Result getUserInfoResult, ConnectUserInfoVO connectUserInfoVO, String userid) {
        Result userInfoResult = new APIResultSupport(true);
        Map<String, Object> data = Maps.newHashMap();
        Map<String, Object> result_value_data = Maps.newHashMap();
        result_value_data.put("id", "");
        result_value_data.put("birthday", getUserInfoResult.getModels().get("birthday").toString());
        result_value_data.put("sex", getUserInfoResult.getModels().get("gender").toString());
        result_value_data.put("nick", getUserInfoResult.getModels().get("uniqname").toString());
        result_value_data.put("location", getUserInfoResult.getModels().get("province").toString() + " " + getUserInfoResult.getModels().get("city").toString());
        result_value_data.put("headurl", getUserInfoResult.getModels().get("avatarurl").toString());
        data.put("result", result_value_data);

        Map<String, Object> original_value_data = Maps.newHashMap();
        original_value_data.put("id", "");
        original_value_data.put("birthday", "");
        original_value_data.put("sex", connectUserInfoVO.getGender());
        original_value_data.put("nick", connectUserInfoVO.getNickname());
        original_value_data.put("location", connectUserInfoVO.getProvince() + " " + connectUserInfoVO.getCity() + " " + connectUserInfoVO.getRegion());
        original_value_data.put("headurl", connectUserInfoVO.getImageURL());
        data.put("original", original_value_data);
        data.put("userid", userid);
        data.put("openid", userid);
        userInfoResult.setModels(data);
        userInfoResult.setMessage("操作成功");
        return userInfoResult;
    }

}
