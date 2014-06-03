package com.sogou.upd.passport.manager.api;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
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
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    public Result deleteTableCache(String key, String tableName) {
        Result result = new APIResultSupport(false);
        try {
            switch (tableName) {
                case "account":
                    accountService.deleteAccountCacheByPassportId(key);
                    break;
                case "account_info":
                    accountInfoService.deleteAccountInfoCacheByPassportId(key);
                    break;
                case "mobile_passportid_mapping":
                    mobilePassportMappingService.deleteMobilePassportMappingCache(key);
                    break;
                default:
                    result.setCode(ErrorUtil.DELETE_CACHE_TABLE_ERROR);
                    return result;
            }
            result.setSuccess(true);
            result.setMessage("key：" + key + " delete " + tableName + " cache success!");
        } catch (ServiceException e) {
            log.error("key：" + key + " delete " + tableName + " cache success!", e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }
}
