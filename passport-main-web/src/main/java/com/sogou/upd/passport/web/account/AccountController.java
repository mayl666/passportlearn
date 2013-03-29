package com.sogou.upd.passport.web.account;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.PostUserProfile;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
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
     * @param mobile   传入的手机号码
     * @param clientid 传入的密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/sendmobilecode", method = RequestMethod.GET)
    @ResponseBody
    public Object sendmobilecode(@RequestParam(defaultValue = "0") int clientid, @RequestParam(defaultValue = "") String mobile)
            throws Exception {
        //参数验证
        boolean empty = hasEmpty(mobile);
        if (empty || clientid == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        //对mobile手机号验证
        Map<String, Object> ret = checkAccount(mobile);
        if (ret != null) return ret;
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientid;
        boolean isExistFromCache = accountService.checkKeyIsExistFromCache(cacheKey);
        Map<String, Object> mapResult = Maps.newHashMap();
        if (isExistFromCache) {
            //更新缓存状态
            mapResult = accountService.updateSmsInfoByAccountFromCache(cacheKey, clientid);
            return mapResult;
        } else {
            boolean isReg = accountService.checkIsRegisterAccount(new Account(mobile));
            if (isReg) {
                //未注册过
                mapResult = accountService.handleSendSms(mobile, clientid);
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
     * @param mobile   传入的手机号码
     * @param clientid 传入的密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/login", method = RequestMethod.POST)
    @ResponseBody
    public Object userlogin(HttpServletRequest request, HttpServletResponse response,
                            @ModelAttribute("postData") PostUserProfile postData, @RequestParam(defaultValue = "0") int clientid,
                            @RequestParam(defaultValue = "") String mobile, @RequestParam(defaultValue = "") String passwd) throws Exception {
        boolean empty = hasEmpty(mobile, passwd);
        if (empty || clientid == 0) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
        }
        return accountService.handleLogin(mobile, passwd, clientid, postData);
    }

    /**
     * 手机账号正式注册调用
     *
     * @param mobile   传入的手机号码
     * @param passwd   传入的密码
     * @param smscode  传入的验证码
     * @param clientid 传入的应用id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/reg", method = RequestMethod.POST)
    @ResponseBody
    public Object mobileUserRegister(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "") String mobile, @RequestParam(defaultValue = "") String passwd,
                                     @RequestParam(defaultValue = "") String smscode, @RequestParam(defaultValue = "0") int clientid) throws Exception {
        //对mobile手机号验证,是否为空，格式及位数是否正确
        Map<String, Object> ret = checkAccount(mobile);
        if (ret != null) return ret;
        //先读缓存，看有没有缓存该手机账号 缓存没有才读数据库表
        String passportId = PassportIDGenerator.generator(mobile, AccountTypeEnum.PHONE.getValue());
        if (!Strings.isNullOrEmpty(passportId)) {     //如果passportId拼串成功，就去缓存里查是否有该手机账号
            long userId = accountService.getUserIdByPassportIdFromCache(passportId);
            if (userId != 0) {      //如果缓存中有该手机账号，则用户已经注册过了！
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            }
        }
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smscode, clientid + "");
        if (!checkSmsInfo) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
        }
        //再读数据库，验证该手机用户是否已经注册过了
//        boolean as = accountService.checkIsRegisterAccount(new Account(mobile, passwd));
        String ip = getIp(request);
//        Account account = null;
//        if (as) {     //如果用户没有被注册，手机号码格式验证通过，并且与验证码匹配，则注册用户，并返回access_token和refresh_token
        Account account = accountService.initialAccount(mobile, passwd, ip, AccountTypeEnum.PHONE.getValue());
        if (account != null) {  //     如果插入account表成功，则插入用户状态表
            //生成token并向account_auth表里插一条用户状态记录
            AccountAuth accountAuth = accountService.initialAccountAuth(account.getId(), account.getPassportId(), clientid);
            if (accountAuth != null) {   //如果用户状态表插入也成功，则说明注册成功
                //往缓存里写入一条Account记录,后一条大史会用到
                accountService.addPassportIdMapUserIdToCache(passportId, account.getId() + "");
//                    accountService.addUserIdMapPassportIdToCache(passportId, account.getId() + "");
                //清除验证码的缓存
                accountService.deleteSmsCache(mobile, String.valueOf(clientid));
                String accessToken = accountAuth.getAccessToken();
                long accessValidTime = accountAuth.getAccessValidTime();
                String refreshToken = accountAuth.getRefreshToken();
                Map<String, Object> mapResult = new HashMap<String, Object>();
                mapResult.put("accessToken", accessToken);
                mapResult.put("accessValidTime", accessValidTime);
                mapResult.put("refreshToken", refreshToken);
                return buildSuccess("用户注册成功！", mapResult);
            } else {
                //用户注册失败
                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
        } else {
            //用户注册失败
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
        }
//        } else {  //否则，不允许手机用户重复注册
//            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
//        }
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
