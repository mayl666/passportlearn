package com.sogou.upd.passport.web.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 账号相关的内部调试接口 User: shipengzhi Date: 13-4-1 Time: 下午5:30 To change this template use File |
 * Settings | File Templates.
 */
@Controller
public class AccoutDebugController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountAuthService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    /**
     * 内部接口，删除手机账号
     */
    @RequestMapping(value = "/internal/debug/deleteAccount", method = RequestMethod.GET)
    @ResponseBody
    public Result deleteAccount(@RequestParam(defaultValue = "") String mobile) throws Exception {
        if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (!Strings.isNullOrEmpty(passportId)) {
                boolean isDeleteAccount = accountService.deleteAccountByPassportId(passportId);
                boolean isDeleteAccountToken = accountAuthService.deleteAccountTokenByPassportId(passportId);
                boolean isDeleteMobilePassportMapping = mobilePassportMappingService.deleteMobilePassportMapping(mobile);
                if (isDeleteAccount && isDeleteAccountToken && isDeleteMobilePassportMapping) {
                    return Result.buildSuccess("删除成功！", null, null);
                } else {
                    return Result.buildError("10000", "删除失败！");
                }
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
            }
        } else {
            return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
        }
    }
}
