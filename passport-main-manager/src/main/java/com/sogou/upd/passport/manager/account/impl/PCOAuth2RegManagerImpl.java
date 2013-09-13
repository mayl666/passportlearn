package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.PCOAuth2RegManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 下午7:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCOAuth2RegManagerImpl implements PCOAuth2RegManager {
    public static final Logger logger = LoggerFactory.getLogger(PCOAuth2RegManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private PCAccountTokenService pcAccountService;

    @Override
    public Result getPairToken(PcPairTokenParams pcPairTokenParams) {
        Result finalResult = new APIResultSupport(false);
        try {
            int clientId = Integer.parseInt(pcPairTokenParams.getAppid());
            String passportId = pcPairTokenParams.getUserid();
            String instanceId = pcPairTokenParams.getTs();
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            return getAccountToken(passportId, instanceId, appConfig);
        } catch (Exception e) {
            logger.error("getPairToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    @Override
    public Result pcAccountRegister(PCOAuth2RegisterParams params, String ip) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Result getAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        Result result = new APIResultSupport(false);
        AccountToken accountToken = pcAccountService.initialOrUpdateAccountToken(passportId, instanceId, appConfig);
        if (accountToken != null) {
            result.setSuccess(true);
            result.setDefaultModel(accountToken);
        } else {
            result.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
        }
        return result;
    }
}
