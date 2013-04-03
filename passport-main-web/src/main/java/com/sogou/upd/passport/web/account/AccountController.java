package com.sogou.upd.passport.web.account;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.Utils;
import com.sogou.upd.passport.web.form.MobileRegParams;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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
    @Inject
    private AccountAuthService accountAuthService;
    @Inject
    private AccountConnectService accountConnectService;

    /**
     * 手机账号获取，重发手机验证码接口
     *
     * @param mobile    传入的手机号码
     * @param client_id 传入的密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/sendmobilecode", method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(@RequestParam(defaultValue = "0") int client_id, @RequestParam(defaultValue = "") String mobile)
            throws Exception {
        //参数验证
        boolean empty = hasEmpty(mobile);
        if (empty || client_id == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        //对mobile手机号验证
        Map<String, Object> ret = checkAccount(mobile);
        if (ret != null) return ret;
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + client_id;
        boolean isExistFromCache = accountService.checkKeyIsExistFromCache(cacheKey);
        Map<String, Object> mapResult = Maps.newHashMap();
        if (isExistFromCache) {
            //更新缓存状态
            mapResult = accountService.updateSmsInfoByAccountFromCache(cacheKey, client_id);
            return mapResult;
        } else {
            Account account = accountService.getAccountByUserName(mobile);
            if (account == null) {
                //未注册过
                mapResult = accountService.handleSendSms(mobile, client_id);
                if (MapUtils.isNotEmpty(mapResult)) {
                    return mapResult;
                } else {
                    return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                }
            } else {
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            }
        }
    }

    /**
     * 手机账号正式注册调用
     *
     * @param request
     * @param response
     * @param regParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/reg", method = RequestMethod.POST)
    @ResponseBody
    public Object mobileUserRegister(HttpServletRequest request, HttpServletResponse response, MobileRegParams regParams) throws Exception {
        // 请求参数校验，必填参数是否正确，手机号码格式是否正确
        String validateResult = Utils.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        String mobile = regParams.getMobile();
        String smscode = regParams.getSmscode();
        int clientId = regParams.getClient_id();
        String password = regParams.getPassword();
        String instanceId = regParams.getInstance_id();

        //先读缓存，看有没有缓存该手机账号 缓存没有才读数据库表
        // todo error 直接查询Account的mobile字段,shipengzhi
        Account existAccount = accountService.getAccountByUserName(mobile);
        if (existAccount != null) {     //说明用户已经注册过了
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
        }
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smscode, clientId + "");
        if (!checkSmsInfo) {
            // todo 这么多return看着好乱，service层抛出problemException，统一捕获
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
        }

        //读数据库，验证该手机用户是否已经注册过了 todo 为什么还要读数据库？
        String ip = getIp(request);
        Account account = accountService.initialAccount(mobile, password, ip, AccountTypeEnum.PHONE.getValue());
        if (account != null) {  //     如果插入account表成功，则插入用户授权信息表
            //生成token并向account_auth表里插一条用户状态记录
            AccountAuth accountAuth = accountAuthService.initialAccountAuth(account.getId(), account.getPassportId(), clientId, instanceId);
            if (accountAuth != null) {   //如果用户授权信息表插入也成功，则说明注册成功
                accountService.addPassportIdMapUserIdToCache(account.getPassportId(), Long.toString(account.getId()));
                //清除验证码的缓存
                accountService.deleteSmsCache(mobile, String.valueOf(clientId));
                String accessToken = accountAuth.getAccessToken();
                long accessValidTime = accountAuth.getAccessValidTime();
                String refreshToken = accountAuth.getRefreshToken();
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", accessToken);
                mapResult.put("expires_time", accessValidTime);
                mapResult.put("refresh_token", refreshToken);
                return buildSuccess("用户注册成功！", mapResult);
            } else {
                //用户注册失败
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
        } else {
            //用户注册失败
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
        }
    }

    /**
     * 找回用户密码
     *
     * @param client_id
     * @param mobile
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/findpwd", method = RequestMethod.GET)
    @ResponseBody
    public Object findPassword(@RequestParam(defaultValue = "0") int client_id, @RequestParam(defaultValue = "") String mobile)
            throws Exception {
        Map<String, Object> map = checkParams(client_id, mobile, null, null);
        if (map != null) return map;
        Account account = accountService.getAccountByUserName(mobile);
        if (account == null) {   //提示该手机用户不存在
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
        }
        Map<String, Object> mapResult = accountService.handleSendSms(mobile, client_id);
        return MapUtils.isNotEmpty(mapResult) == true ? mapResult : ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
    }

    /**
     * 重置密码
     *
     * @param client_id
     * @param mobile
     * @param smscode
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPassword(@RequestParam(defaultValue = "0") int client_id, @RequestParam(defaultValue = "") String mobile,
                                @RequestParam(defaultValue = "") String smscode, @RequestParam(defaultValue = "") String password,
                                @RequestParam(defaultValue = "") String instance_id) throws Exception {

        Map<String, Object> map = checkParams(client_id, mobile, smscode, password);
        if (map != null) return map;
        //重置密码
        boolean resetPwd = accountService.resetPassword(mobile, password);
        //根据mobile查询手机用户信息
        Account account = accountService.getAccountByUserName(mobile);
        //先更新当前客户端实例对应的access_token和refresh_token，再异步更新该用户其它客户端的两个token
        AccountAuth accountAuthResult = null;
        if (account != null) {
            accountAuthResult = accountAuthService.updateAccountAuth(account.getId(), account.getPassportId(), client_id, instance_id);
        }
        //TODO 异步更新该用户其它状态信息
        if (resetPwd == true && accountAuthResult != null) {
            //清除验证码的缓存
            accountService.deleteSmsCache(mobile, String.valueOf(client_id));
            return ErrorUtil.buildSuccess("重置密码成功", null);
        } else {
            return ErrorUtil.buildExceptionError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
        }

    }

    /**
     * 验证找回和重置密码时，各种参数的合法性
     *
     * @param client_id
     * @param mobile
     * @param smscode
     * @param password
     * @return
     */
    private Map<String, Object> checkParams(int client_id, String mobile, String smscode, String password) {
        //先验证参数是否为空
        boolean empty = hasEmpty(mobile, smscode, password);
        if (empty || client_id == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        //其次，手机号码格式是否正确
        if (mobile != null) {
            Map<String, Object> ret = checkAccount(mobile);
            if (ret != null) return ret;
        }
        //再者，密码格式是否正确
        if (password != null) {
            if (!checkPasswd(password)) {
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PASSWDFORMAT);
            }
        }
        if (mobile != null && smscode != null && client_id != 0) {
            //最后,验证手机号与验证码是否匹配
            boolean smscodeValid = accountService.checkSmsInfoFromCache(mobile, smscode, client_id + "");
            if (!smscodeValid) {
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }
        }
        return null;
    }

    /**
     * 验证密码格式是否正确
     *
     * @param passwd
     * @return
     */
    private boolean checkPasswd(String passwd) {
        return StringUtils.isAsciiPrintable(passwd) && passwd.length() >= 6 && passwd.length() <= 16;
    }

    /**
     * 验证手机号码是否为空，格式是否正确
     *
     * @param mobile
     * @return
     */
    private Map<String, Object> checkAccount(String mobile) {
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
