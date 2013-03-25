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
//        //参数验证
//        boolean empty = hasEmpty(mobile);
//        if (empty || appkey == 0) {
//            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
//        }
//        //对mobile手机号验证
//        Map<String, Object> ret = checkAccount(mobile);
//        if (ret != null) return ret;
        checkMobile(mobile, appkey);
        //判断账号是否被注册  todo 先缓存读取
        boolean account = accountService.checkIsRegisterAccount(new Account(mobile));
        if (account == true) {

        } else {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
        }
        return buildSuccess(null, null);
    }

    /**
     * 手机账号正式注册调用
     *
     * @param mobile  传入的手机号码
     * @param passwd  传入的密码
     * @param smsCode 传入的验证码
     * @param appkey  传入的应用id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/account", method = RequestMethod.POST)
    @ResponseBody
    public Object mobileUserRegister(@RequestParam(defaultValue = "") String mobile, @RequestParam(defaultValue = "") String passwd,
                                     @RequestParam(defaultValue = "") String smsCode, @RequestParam(defaultValue = "0") int appkey) throws Exception {
        //验证手机号码是否为空，格式及位数是否正确
        checkMobile(mobile, appkey);
        //验证手机号码与验证码是否匹配
        //todo add service implement of app table
        //验证密码是否明文传送
        //todo add service implement of validate
        //验证该手机用户是否已经注册过了
        boolean account = accountService.checkIsRegisterAccount(new Account(mobile, passwd));
        if (account == true) {     //如果用户没有被注册，则注册用户，并返回access_token和refresh_token
            //todo 这里分跳转情况，1，跳转到登录页面，相当于第一次登录；2，自动登录，相当于第N次登录
        } else {                    //否则，不允许手机用户重复注册
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
        }
        //todo 调用生成access_token和refresh_token方法生成并返回给用户
        return null;
    }

    /**
     * 验证传入的手机参数及appkey是否正确，此为公共部分提出复用
     *
     * @param mobile
     * @param appkey
     * @return
     */
    private Map<String, Object> checkMobile(String mobile, int appkey) {
        //参数验证
        boolean empty = hasEmpty(mobile);
        if (empty || appkey == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        //对mobile手机号验证
        Map<String, Object> ret = checkAccount(mobile);
        if (ret != null) return ret;
        return null;
    }

    /**
     * 验证手机号码是否为空，格式是否正确
     *
     * @param mobile
     * @return
     */
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
