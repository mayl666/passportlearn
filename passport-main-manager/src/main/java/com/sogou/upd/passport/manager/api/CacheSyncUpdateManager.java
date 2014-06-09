package com.sogou.upd.passport.manager.api;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据同步中缓存更新方法
 * User: shipengzhi
 * Date: 14-6-3
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CacheSyncUpdateManager {

    private static Logger log = LoggerFactory.getLogger(CacheSyncUpdateManager.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;

    public Result deleteTableCache(String passportId) {
        Result result = new APIResultSupport(false);
        try {
            boolean isDelAccount = accountService.deleteAccountCacheByPassportId(passportId);
            boolean isDelAccountInfo = accountInfoService.deleteAccountInfoCacheByPassportId(passportId);
            if (isDelAccount && isDelAccountInfo) {
//                    mobilePassportMappingService.deleteMobilePassportMappingCache(key);
                result.setSuccess(true);
                result.setMessage("passportId：" + passportId + " delete cache success!");
            } else {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            }
        } catch (ServiceException e) {
            log.error("passportId：" + passportId + " delete cache fail!", e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }
}

