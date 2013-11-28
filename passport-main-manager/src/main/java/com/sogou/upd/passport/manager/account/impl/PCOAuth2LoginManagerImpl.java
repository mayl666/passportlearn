package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.PCOAuth2LoginManager;
import com.sogou.upd.passport.manager.form.PCOAuth2LoginParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-27
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCOAuth2LoginManagerImpl implements PCOAuth2LoginManager {
    @Autowired
    private LoginManager loginManager;
    @Autowired
    SnamePassportMappingService snamePassportMappingService;

    @Override
    public Result accountLogin(PCOAuth2LoginParams loginParams, String ip, String scheme){
        String sogou_passportId = loginParams.getLoginname();
        Result sohu_result=null;
        if(AccountDomainEnum.isIndivid(sogou_passportId)){
            String sohu_passportId = snamePassportMappingService.queryPassportIdBySnameOrPhone(sogou_passportId);
            if(!Strings.isNullOrEmpty(sohu_passportId)){
                sohu_result =  authuser(loginParams,ip,scheme,sohu_passportId);
            }
        }
        Result sogou_result = authuser(loginParams,ip,scheme,sogou_passportId);
        if(sohu_result == null || !sohu_result.isSuccess()){
             return sogou_result;
        }else if(sohu_result.isSuccess() && !sogou_result.isSuccess()) {
            return sohu_result;
        }else {
            //二者都能验证成功，产生账号冲突，人工解决
            Result error_result = new APIResultSupport(false);
            error_result.setCode(ErrorUtil.ERR_CODE_ERROR_ACCOUNT);
            error_result.setMessage("账号冲突或者异常，请到论坛问题反馈区找回账号");
            return error_result;
        }
    }

    private Result authuser(PCOAuth2LoginParams loginParams, String ip, String scheme,String passportId){
        WebLoginParams webLoginParams = new WebLoginParams();
        webLoginParams.setUsername(passportId);
        webLoginParams.setPassword(loginParams.getPwd());
        webLoginParams.setPwdtype(loginParams.getPwdtype());
        webLoginParams.setCaptcha(loginParams.getCaptcha());
        webLoginParams.setToken(loginParams.getToken());
        webLoginParams.setClient_id(String.valueOf(loginParams.getClient_id()));
        Result result = loginManager.accountLogin(webLoginParams, ip, scheme);
        return result;
    }
}
