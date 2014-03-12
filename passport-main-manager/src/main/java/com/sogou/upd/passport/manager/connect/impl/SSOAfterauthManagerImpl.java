package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.manager.connect.SSOAfterauthManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.MappTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SSOAfterauthManagerImpl implements SSOAfterauthManager{
    private static Logger logger = LoggerFactory.getLogger(SSOAfterauthManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private SessionServerManager sessionServerManager;

    @Override
    public Result handleSSOAfterauth(HttpServletRequest req,String providerStr) {
        Result result = new APIResultSupport(false);

        try {
            String openId = req.getParameter("openid");
            String accessToken = req.getParameter("access_token");
            long expires_in = Long.parseLong(req.getParameter("expires_in"));
            int client_id = Integer.parseInt(req.getParameter("client_id"));
            int isthird = Integer.parseInt(req.getParameter("isthird"));
//            String instance_id = req.getParameter("instance_id");

            int provider = AccountTypeEnum.getProvider(providerStr);

            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }

            //根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(client_id, provider);
            if (connectConfig == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            // 获取第三方个人资料
            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);

            //isthird=0或1；0表示去搜狗通行证个人信息，1表示获取第三方个人信息
            if (isthird == 0) {
                if (provider == AccountTypeEnum.QQ.getValue()) {
                    String passportId=openId+"@qq.sohu.com";
                    ObtainAccountInfoParams params=new ObtainAccountInfoParams();
                    params.setUsername(passportId);
                    params.setClient_id(String.valueOf(client_id));
                    params.setFields("uniqname,sex");
                    result = accountInfoManager.getUserInfo(params);
                    if(result.isSuccess()){
                        String img180= (String) result.getModels().get("img_180");
                        String img50= (String) result.getModels().get("img_50");
                        String img30= (String) result.getModels().get("img_30");
                        String uniqname= (String) result.getModels().get("uniqname");
                        String sex= (String) result.getModels().get("sex");

                        result.getModels().put("large_avatar",Strings.isNullOrEmpty(img180)?"":img180) ;
                        result.getModels().put("mid_avatar",Strings.isNullOrEmpty(img50)?"":img50) ;
                        result.getModels().put("tiny_avatar",Strings.isNullOrEmpty(img30)?"":img30) ;
                        result.getModels().put("uniqname",Strings.isNullOrEmpty(uniqname)?"":uniqname) ;
                        result.getModels().put("sex", Strings.isNullOrEmpty(sex) ? 0 : Integer.parseInt(sex)) ;
                    }
                }
            }else {
                if(connectUserInfoVO!=null){
                    result.getModels().put("large_avatar",connectUserInfoVO.getAvatarLarge()) ;
                    result.getModels().put("mid_avatar",connectUserInfoVO.getAvatarMiddle()) ;
                    result.getModels().put("tiny_avatar",connectUserInfoVO.getAvatarSmall()) ;
                    result.getModels().put("uniqname",connectUserInfoVO.getNickname()) ;
                    result.getModels().put("sex",connectUserInfoVO.getGender()) ;
                }
            }

            OAuthTokenVO oAuthTokenVO=null;
            String uniqname = openId;
            if (connectUserInfoVO != null) {
                oAuthTokenVO=new OAuthTokenVO();
                uniqname = connectUserInfoVO.getNickname();
                oAuthTokenVO.setNickName(uniqname);
                oAuthTokenVO.setConnectUserInfoVO(connectUserInfoVO);
                oAuthTokenVO.setAccessToken(accessToken);
                oAuthTokenVO.setOpenid(openId);
                oAuthTokenVO.setExpiresIn(expires_in);

                // 创建第三方账号
                Result connectAccountResult = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);
                if(connectAccountResult.isSuccess()){
                    ConnectToken connectToken=(ConnectToken)connectAccountResult.getModels().get("connectToken");

                    String passportId= connectToken.getPassportId();
//                result.getModels().put("passport_id", passportId);
                    //写session 数据库
                    Result sessionResult = sessionServerManager.createSession(passportId);
                    String sgid = null;
                    if (sessionResult.isSuccess()) {
                        sgid = (String) sessionResult.getModels().get("sgid");
                        if (!Strings.isNullOrEmpty(sgid)) {
                            result.getModels().put("sgid",sgid);
                            result.setSuccess(true);
                            result.setMessage("success");
                            removeParam(result);
                        }
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_SSO_After_Auth_FAILED);
                    }
                }else{
                    result.setCode(ErrorUtil.ERR_CODE_SSO_After_Auth_FAILED);
                }
            }else {
                result.setCode(ErrorUtil.ERR_CODE_SSO_After_Auth_FAILED);
            }

        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
       } catch (OAuthProblemException ope) {
            logger.error("handle oauth authroize code error!", ope);
            result = buildErrorResult(ope.getError(), ope.getDescription());
        } catch (Exception exp) {
            logger.error("handle oauth authroize code system error!", exp);
        }

        return result;
    }
    private void removeParam(Result result){
        result.getModels().remove("img_30");
        result.getModels().remove("img_50");
        result.getModels().remove("img_180");
        result.getModels().remove("avatarurl");
    }
    private Result buildErrorResult(String errorCode, String errorText) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        return result;
    }
}

