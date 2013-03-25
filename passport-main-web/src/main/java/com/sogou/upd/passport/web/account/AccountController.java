package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

/**
 * 移动用户注册登录
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Inject
    private AccountService accountService;


    @RequestMapping(value = "/v2/authcode", method = RequestMethod.GET)
    @ResponseBody
    public Object sendPhoneCode(@RequestParam(defaultValue = "0") int appkey, @RequestParam(defaultValue = "") String mobile)
            throws Exception {
        //参数验证
        boolean empty = hasEmpty(mobile);
        if (empty || appkey == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        //对mobile手机号验证
        Map<String, Object> ret = checkAccount(mobile);
        if (ret != null) return ret;
        //判断账号是否被缓存
        boolean isExistFromCache = accountService.checkIsExistFromCache(mobile);
        if (isExistFromCache) {
            //更新缓存
        } else {
            boolean isReg = accountService.checkIsRegisterAccount(new Account(mobile));
            if (isReg) {
                accountService.handleSendSms(mobile,appkey);
            } else {
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            }
        }


        return buildSuccess(null, null);
    }

    private Map<String, Object> checkAccount(String mobile) {
        boolean empty = hasEmpty(mobile);
        if (empty) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        if (!PhoneUtil.verifyPhoneNumberFormat(mobile)) {
            return ErrorUtil
                    .buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
        }
        return null;
    }

    /**
     * 验证参数是否有空参数
     *
     * @param args
     * @return
     */
    protected boolean hasEmpty(String... args) {

        if (args == null) {
            return false;
        }

        Object[] argArray = getArguments(args);
        for (Object obj : argArray) {
            if (obj instanceof String && StringUtils.isEmpty((String) obj)) return true;
        }
        return false;
    }

    private Object[] getArguments(Object[] varArgs) {
        if (varArgs.length == 1 && varArgs[0] instanceof Object[]) {
            return (Object[]) varArgs[0];
        } else {
            return varArgs;
        }
    }
}
