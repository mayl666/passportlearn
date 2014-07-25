package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.EmailSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午11:04
 * To change this template use File | Settings | File Templates.
 */
@Component("sgBindApiManager")
public class SGBindApiManagerImpl implements BindApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGBindApiManagerImpl.class);

    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private LoginApiManager sgLoginApiManager;

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        Result result;
        String passportId = bindEmailApiParams.getUserid();
        int clientId = bindEmailApiParams.getClient_id();
        String password = bindEmailApiParams.getPassword();
        String oldEmail = bindEmailApiParams.getOldbindemail();
        String newEmail = bindEmailApiParams.getNewbindemail();
        AuthUserApiParams authParams = new AuthUserApiParams(clientId, passportId, password);
        result = sgLoginApiManager.webAuthUser(authParams);    //验证密码
        if (!result.isSuccess()) {
            return result;
        }
        String bindEmail = accountInfoService.queryBindEmailByPassportId(passportId);
        if (!Strings.isNullOrEmpty(bindEmail) && !bindEmail.equals(oldEmail)) {   // 验证用户输入原绑定邮箱
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
            return result;
        }
        HashMap<String, Object> urlMap = Maps.newHashMap();
        urlMap.put("passportId", passportId);
        urlMap.put("clientId", clientId);
        urlMap.put("ru", bindEmailApiParams.getRu());
        if (!emailSenderService.sendEmail(urlMap, AccountClientEnum.web, AccountModuleEnum.SECURE, newEmail, true)) {
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
            return result;
        }
        result.setSuccess(true);
        result.setMessage("绑定邮箱验证邮件发送成功！");
        return result;
    }

    @Override
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        Result result = new APIResultSupport(false);
        String mobile = baseMoblieApiParams.getMobile();

        String userId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
        if (userId == null) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
            return result;
        }
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setDefaultModel("userid", userId);
        return result;
    }

    @Override
    public Result bindMobile(String passportId, String newMobile, Account account) {
        return null;
    }

    @Override
    public Result modifyBindMobile(String passportId, String newMobile) {
        return null;
    }

    @Override
    public Result unBindMobile(String mobile) {
        return null;
    }

}
