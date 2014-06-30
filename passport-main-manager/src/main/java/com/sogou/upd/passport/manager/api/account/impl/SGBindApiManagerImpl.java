package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private OperateTimesService operateTimesService;

    // TODO:验证邮件的Manager
    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        Result result = new APIResultSupport(false);
        String userId = bindEmailApiParams.getUserid();
        int clientId = bindEmailApiParams.getClient_id();
        String password = bindEmailApiParams.getPassword();
        String oldEmail = bindEmailApiParams.getOldbindemail();
        String newEmail = bindEmailApiParams.getNewbindemail();

        AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(userId);
        if (accountInfo != null) {
            String emailBind = accountInfo.getEmail();
            if (!Strings.isNullOrEmpty(emailBind) && !emailBind.equals(oldEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
                return result;
            }
        }

        result = accountService.verifyUserPwdVaild(userId, password, true);
        if(!result.isSuccess()){
            return result;
        }

        if (!emailSenderService.sendBindEmail(userId, clientId, AccountModuleEnum.SECURE, newEmail, bindEmailApiParams.getRu())) {
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
    public Result bindMobile(String passportId,String newMobile){
        return null;
    }

    @Override
    public Result modifyBindMobile(String passportId, String newMobile) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result unBindMobile(String mobile){
        return null;
    }

}
