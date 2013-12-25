package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.WapTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-12
 * Time: 下午7:41
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WapLoginManagerImpl implements WapLoginManager {
    private static final Logger logger = LoggerFactory.getLogger(WapLoginManagerImpl.class);

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapTokenService wapTokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;

    @Override
    public Result accountLogin(WapLoginParams loginParams, String ip) {
        Result result = new APIResultSupport(false);
        String username = loginParams.getUsername();
        String password = loginParams.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        try {
            //验证验证码
            result = checkCaptchaVaild(username, ip, loginParams.getClient_id(), loginParams.getCaptcha(), loginParams.getToken());
            if (!result.isSuccess()) {
                return result;
            }
            result = loginManager.authUser(username, ip, pwdMD5);
            if (result.isSuccess()) {
//                String userId = result.getModels().get("userid").toString();
//                String token = wapTokenService.saveWapToken(userId);
//                result.setDefaultModel("token", token);
                //写session 数据库
                Result sessionResult = sessionServerManager.createSession(passportId);
                String sgid=null;
                if(sessionResult.isSuccess()){
                    sgid= (String) sessionResult.getModels().get("sgid");
                    if (!Strings.isNullOrEmpty(sgid)) {
                        result.setDefaultModel("sgid", sgid);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("accountLogin fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public Result authtoken(String token) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = wapTokenService.getPassprotIdByToken(token);
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setSuccess(true);
                result.setDefaultModel("userid", passportId);
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public Result checkCaptchaVaild(String username, String ip, String clientId,String captchaCode,String token ) {
        Result result = new APIResultSupport(true);
        //校验验证码
        if (needCaptchaCheck(clientId, username, ip)) {
            if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                logger.info("[checkCaptchaVaild captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captchaCode);
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                return result;
            }
        }
        return result;
    }

    @Override
    public Result passThroughQQ(String sgid,String accessToken,String openId) {
        Result result = new APIResultSupport(true);
        try {
            //根据获取第三方个人资料验证token的有效性
            int provider = AccountTypeEnum.QQ.getValue();
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(CommonConstant.SGPP_DEFAULT_CLIENTID, provider);
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);

            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
            if (connectUserInfoVO == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                return result;
            }
            String nickname=connectUserInfoVO.getNickname();
            String shPassportId = openId + "@qq.sohu.com";
            if(!Strings.isNullOrEmpty(sgid)){
                 //session server获取passportid
                Result sessionResult = sessionServerManager.getPassportIdBySgid(sgid);
                if(sessionResult.isSuccess()){
                    String passportId= (String) sessionResult.getModels().get("passport_id");
                    if(Strings.isNullOrEmpty(passportId)){
                        //sgid失效，重新生成sgid
                        result=bulidSgid(accessToken,shPassportId,nickname) ;
                    }else if(passportId.equals(shPassportId)){
                        result.setSuccess(true);
                        result.getModels().put("sgid",sgid);
                    }
                }
            }else {
                result=bulidSgid(accessToken,shPassportId,nickname) ;
            }
        }catch (Exception e){
            logger.error("passThroughQQ error:",e);
        }

        return result;
    }

    private Result bulidSgid(String accessToken,String openId,String nickname) {
        // sohu创建第三方账号
        String provider= AccountTypeEnum.QQ.toString();
        OAuthTokenVO oAuthTokenVO=bulidOAuthTokenVO(accessToken, openId,nickname);
        Result connectAccountResult = proxyConnectApiManager.buildConnectAccount(provider, oAuthTokenVO);

        if(connectAccountResult.isSuccess()){
            //创建sgid
            Result sessionResult = sessionServerManager.createSession(openId);
            String sgid = null;
            if (sessionResult.isSuccess()) {
                sgid = (String) sessionResult.getModels().get("sgid");
                if (!Strings.isNullOrEmpty(sgid)) {
                    sessionResult.setDefaultModel("sgid", sgid);
                }
            }
            return sessionResult;
        } else {
            return connectAccountResult;
        }
    }

    private OAuthTokenVO bulidOAuthTokenVO(String accessToken,String openId,String nickname){
        OAuthTokenVO oAuthTokenVO=new OAuthTokenVO();
        oAuthTokenVO.setAccessToken(accessToken);
        oAuthTokenVO.setOpenid(openId);
        oAuthTokenVO.setNickName(nickname);
        return oAuthTokenVO;
    }

    @Override
    public Result removeSession(String sgid) {
        Result result = sessionServerManager.removeSession(sgid);
        return result;
    }

    @Override
    public boolean needCaptchaCheck(String client_id, String username, String ip) {
        if (operateTimesService.loginFailedTimesNeedCaptcha(username, ip)) {
            return true;
        }
        return false;
    }

    @Override
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incLoginTimes(username, ip,true);
    }

}
