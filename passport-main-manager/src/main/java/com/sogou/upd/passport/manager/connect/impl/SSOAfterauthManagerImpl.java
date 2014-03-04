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
    private UserOpenApiManager sgUserOpenApiManager;
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
            String instance_id = req.getParameter("instance_id");

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
                    params.setFields("uniqname");
                    result = accountInfoManager.getUserInfo(params);
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
            }
            // 创建第三方账号
            Result connectAccountResult = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);
            if(connectAccountResult.isSuccess()){
                ConnectToken connectToken=(ConnectToken)connectAccountResult.getDefaultModel();
                //写session 数据库
                Result sessionResult = sessionServerManager.createSession(connectToken.getPassportId());
                String sgid = null;
                if (sessionResult.isSuccess()) {
                    sgid = (String) sessionResult.getModels().get("sgid");
                    if (!Strings.isNullOrEmpty(sgid)) {
//                        result.setSuccess(true);
//                        result.getModels().put("sgid", sgid);
                    }
                } else {
//                    result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:" + userId);
                }
            }
            SSOAfterAuthResult ssoAfterAuthResult=new SSOAfterAuthResult();


        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
//            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
//            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
       } catch (OAuthProblemException ope) {
            logger.error("handle oauth authroize code error!", ope);
//            result = buildErrorResult(type, ru, ope.getError(), ope.getDescription());
        } catch (Exception exp) {
//            logger.error("handle oauth authroize code system error!", exp);
//            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!");
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

class SSOAfterAuthResult{
    private String passportId;
    private String sgid;
    private String uniqname;
    private String sex;
    private String large_avatar;
    private String mid_avatar;
    private String tiny_avatar;

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getSgid() {
        return sgid;
    }

    public void setSgid(String sgid) {
        this.sgid = sgid;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }

    public String getMid_avatar() {
        return mid_avatar;
    }

    public void setMid_avatar(String mid_avatar) {
        this.mid_avatar = mid_avatar;
    }

    public String getTiny_avatar() {
        return tiny_avatar;
    }

    public void setTiny_avatar(String tiny_avatar) {
        this.tiny_avatar = tiny_avatar;
    }
}
