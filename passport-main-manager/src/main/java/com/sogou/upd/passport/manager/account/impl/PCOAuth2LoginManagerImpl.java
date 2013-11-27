package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.Result;
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
        String passportId = loginParams.getLoginname();
        //可能存在冲突账号，特殊处理
        if(AccountDomainEnum.isPhoneOrIndivid(passportId)){
            //todo 判断是否在冲突表中，相关处理

            //查询映射关系
            String sohuPassportId = snamePassportMappingService.queryPassportIdBySname(passportId);
            if(!Strings.isNullOrEmpty(sohuPassportId)){
                passportId =  sohuPassportId;
            }
        }
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
