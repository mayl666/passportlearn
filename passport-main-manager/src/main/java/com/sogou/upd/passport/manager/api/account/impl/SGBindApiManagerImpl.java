package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;

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

    @Override
    public Result bindMobile(BindMobileApiParams bindMobileApiParams) {
        Result result = new APIResultSupport(false);
        String userid = bindMobileApiParams.getUserid();
        String mobile = bindMobileApiParams.getMobile();
        int clientId = bindMobileApiParams.getClient_id();

        Account account = accountService.queryNormalAccount(userid);
        if (account == null) {
            result.setCode(ErrorUtil.INVALID_ACCOUNT);
            return result;
        }

        if (!mobilePassportMappingService.initialMobilePassportMapping(mobile, userid)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
            return result;
        }

        if (!accountService.modifyMobile(account, mobile)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            return result;
        }

        result.setSuccess(true);
        result.setMessage("绑定手机成功！");
        return result;
    }

    @Override
    public Result unbindMobile(BaseMoblieApiParams baseMoblieApiParams) {
        Result result = new APIResultSupport(false);
        String mobile = baseMoblieApiParams.getMobile();
        int clientId = baseMoblieApiParams.getClient_id();

        String userId = mobilePassportMappingService.queryPassportIdByMobile(mobile);

        Account account = accountService.queryNormalAccount(userId);
        if (account == null) {
            result.setCode(ErrorUtil.INVALID_ACCOUNT);
            return result;
        }

        if (!mobilePassportMappingService.deleteMobilePassportMapping(mobile)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
            return result;
        }

        if (!accountService.modifyMobile(account, null)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            return result;
        }

        result.setSuccess(true);
        result.setMessage("绑定手机成功！");
        return result;
    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getPassportIdFromMobile(MobileBindPassportIdApiParams mobileBindPassportIdApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result queryPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        // TODO 当Manager里方法只调用一个service时，需要把service的返回值改为Result
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
