package com.sogou.upd.passport.web.account;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.PostUserProfile;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.web.BaseController;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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
    private AccountConnectService accountConnectService;

    /**
     * 手机账号获取，重发手机验证码接口
     *
     * @param mobile 传入的手机号码
     * @param appkey 传入的密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/authcode", method = RequestMethod.GET)
    @ResponseBody
    public Object authcode(@RequestParam(defaultValue = "0") int appkey, @RequestParam(defaultValue = "") String mobile)
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
        String cacheKey = mobile + "_" + appkey;
        boolean isExistFromCache = accountService.checkIsExistFromCache(cacheKey);
        Map<String, Object> mapResult = Maps.newHashMap();
        if (isExistFromCache) {
            //更新缓存状态
            mapResult = accountService.updateCacheStatusByAccount(cacheKey);
            return mapResult;
        } else {
            boolean isReg = accountService.checkIsRegisterAccount(new Account(mobile));
            if (isReg) {
                //未注册过
                mapResult = accountService.handleSendSms(mobile, appkey);
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
     * 手机账号登录接口
     *
     * @param mobile 传入的手机号码
     * @param appkey 传入的密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/userlogin", method = RequestMethod.POST)
    @ResponseBody
    public Object userlogin(HttpServletRequest request, HttpServletResponse response,
                            @ModelAttribute("postData") PostUserProfile postData, @RequestParam(defaultValue = "0") int appkey,
                            @RequestParam(defaultValue = "") String mobile, @RequestParam(defaultValue = "") String passwd,
                            @RequestParam(defaultValue = "") String access_token) throws Exception {
        boolean empty = hasEmpty(mobile, passwd, access_token);
        if (empty || appkey == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        return accountService.handleLogin(mobile, passwd, access_token, appkey, postData);
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
    public Object mobileUserRegister(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "") String mobile, @RequestParam(defaultValue = "") String passwd,
                                     @RequestParam(defaultValue = "") String smsCode, @RequestParam(defaultValue = "0") int appkey) throws Exception {
        String ip = getIp(request);
        //验证手机号码是否为空，格式及位数是否正确
        Map<String, Object> mapAccount = checkAccount(mobile);
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoCache(mobile, smsCode, appkey + "");
        //验证密码是否明文传送
        //TODO 调用密码是否明文传送的接口
        //验证该手机用户是否已经注册过了
        boolean as = accountService.checkIsRegisterAccount(new Account(mobile, passwd));
        Account account = null;
        if (as == true) {     //如果用户没有被注册，则注册用户，并返回access_token和refresh_token
            account = accountService.initialAccount(mobile, passwd, ip, AccountTypeEnum.PHONE.getValue());
            if (account != null) {  //如果对象不为空，说明注册成功
                //TODO 往缓存里写入一条Account记录
                //往account_auth表里插一条用户状态记录
                AccountAuth accountAuth = accountService.initialAccountAuth(account, appkey);
                if (accountAuth != null) {
                    //TODO 往缓存里写入一条AccountAuth记录
                    String accessToken = accountAuth.getAccessToken();
                    String refreshToken = accountAuth.getRefreshToken();
                    Map<String, Object> mapResult = new HashMap<String, Object>();
                    mapResult.put("account", account);
                    mapResult.put("accessToken", accessToken);
                    mapResult.put("refreshToken", refreshToken);
                    return buildSuccess("用户注册成功！", mapResult);
                } else {
                    //  TODO  这个地方是异常抛出来还是build状态码及提示信息，待定！
                }
            } else {
                //用户注册失败 TODO 同上，待定！
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
        } else {  //否则，不允许手机用户重复注册
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
        }
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
