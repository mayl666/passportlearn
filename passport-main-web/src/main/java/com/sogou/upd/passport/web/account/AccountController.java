package com.sogou.upd.passport.web.account;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.account.parameters.RegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.form.MobileRegParams;
import com.sogou.upd.passport.web.form.MoblieCodeParams;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
    private AccountSecureManager accountSecureManager;

    @Inject
    private AccountRegManager accountRegManager;

    @Inject
    private AccountService accountService;
    @Inject
    private AccountAuthService accountAuthService;
    @Inject
    private AccountConnectService accountConnectService;

    /**
     * 手机账号获取，重发手机验证码接口
     *
     * @param reqParams 传入的参数
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/sendmobilecode", method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(MoblieCodeParams reqParams)
            throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        String mobile = reqParams.getMobile();
        int clientId = reqParams.getClient_id();

        Result result = accountSecureManager.sendMobileCode(mobile, clientId);


        return result;

    }

    /**
     * 手机账号正式注册调用
     *
     * @param request
     * @param regParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/reg", method = RequestMethod.POST)
    @ResponseBody
    public Object mobileUserRegister(HttpServletRequest request, MobileRegParams regParams) throws Exception {
        // 请求参数校验，必填参数是否正确，手机号码格式是否正确
        String validateResult = ControllerHelper.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        RegisterParameters rp = new RegisterParameters();
        rp.setClientId(regParams.getClient_id());
        rp.setInstanceId(regParams.getInstance_id());
        rp.setMobile(regParams.getMobile());
        rp.setPassword(regParams.getPassword());
        rp.setSmscode(regParams.getSmscode());
        rp.setIp(getIp(request));

        Result result = accountRegManager.mobileRegister(rp);

        return result;
    }

    /**
     * 找回用户密码
     *
     * @param reqParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/findpwd", method = RequestMethod.GET)
    @ResponseBody
    public Object findPassword(MoblieCodeParams reqParams)
            throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        String mobile = reqParams.getMobile();
        int clientId = reqParams.getClient_id();

        Account account = accountService.getAccountByUserName(mobile);
        if (account == null) {   //提示该手机用户不存在
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
        }
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        boolean isExistFromCache = accountService.checkCacheKeyIsExist(cacheKey);
        Map<String, Object> mapResult;
        if (isExistFromCache) {
            //更新缓存状态
            //todo
//            mapResult = accountService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
//            return mapResult;
            return null;
        } else {
            //todo
//            mapResult = accountService.handleSendSms(mobile, clientId);
        }
        //todo
//        return MapUtils.isNotEmpty(mapResult) ? mapResult : ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
        return null;
    }

    /**
     * 重置密码
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/mobile/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPassword(MobileRegParams reqParams) throws Exception {
        // 校验参数
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        String mobile = reqParams.getMobile();
        String smscode = reqParams.getSmscode();
        int clientId = reqParams.getClient_id();
        String password = reqParams.getPassword();
        String instanceId = reqParams.getInstance_id();

        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smscode, clientId + "");
        if (!checkSmsInfo) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
        }

        //重置密码
        Account account = accountService.resetPassword(mobile, password);
        //先更新当前客户端实例对应的access_token和refresh_token，再异步更新该用户其它客户端的两个token
        AccountAuth accountAuthResult = null;
        if (account != null) {
            accountAuthResult = accountAuthService.updateAccountAuth(account.getId(), account.getPassportId(), clientId, instanceId);
            //TODO 存在分库分表问题
            accountAuthService.asynUpdateAccountAuthBySql(mobile, clientId, instanceId);
        }
        if (accountAuthResult != null) {
            //清除验证码的缓存
            accountService.deleteSmsCache(mobile, String.valueOf(clientId));
            return buildSuccess("重置密码成功", null);
        } else {
            return ErrorUtil.buildExceptionError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
        }

    }

}
