package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.service.account.PCAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCAccountManagerImpl implements PCAccountManager{
    private static final Logger logger = LoggerFactory.getLogger(PCAccountManagerImpl.class);
    @Autowired
    private PCAccountService pcAccountService;

    @Override
    public Result authToken(PcAuthTokenParams authPcTokenParams) {
        Result result = new APIResultSupport(false);
        try {
            //验证accessToken
            String key = authPcTokenParams.getUserid() +"_"+ authPcTokenParams.getTs();
            if(pcAccountService.checkToken(key,authPcTokenParams.getToken())){
                result.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error("authToken fail", e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }
}
