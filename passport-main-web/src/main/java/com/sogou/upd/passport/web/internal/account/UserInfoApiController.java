package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.converters.CustomDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 下午3:04
 */
@Controller
@RequestMapping("/internal/account")
public class UserInfoApiController extends BaseController {

    //TODO 需要改为配置的，但目前配置有问题
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(true));
    }

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private ConfigureManager configureManager;

    /**
     * 获取用户基本信息
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/userinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object getUserInfo(GetUserInfoApiparams params, HttpServletRequest request) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 调用内部接口
        result = sgUserInfoApiManager.getUserInfo(params);
        UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
        userOperationLog.putOtherMessage("fields", params.getFields());
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }


    /**
     * 更新用户基本信息
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/updateuserinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object updateUserInfo(UpdateUserInfoApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 调用内部接口
        result = sgUserInfoApiManager.updateUserInfo(params);
        UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), String.valueOf(params.getClient_id()), result.getCode(), params.getModifyip());
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    /**
     * 检查用户昵称是否唯一
     *
     * @param params
     * @param request
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/checkuniqname", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUniqname(UpdateUserUniqnameApiParams params, HttpServletRequest request) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //调用检查昵称是否唯一的内部接口
        result = sgUserInfoApiManager.checkUniqName(params);
        UserOperationLog userOperationLog = new UserOperationLog(params.getUniqname(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    /**
     * 检查账号是否存在
     * 账号类型为：xxx@sogou.com、搜狐域账号、外域邮箱账号、xxx@{provider}.sohu.com
     * 手机号会返回"userid错误"
     *
     * @param request
     * @param params
     * @return
     */
//    @InterfaceSecurity
    @RequestMapping(value = "/checkuser", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUser(HttpServletRequest request, CheckUserApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id是否存在
            int clientId = params.getClient_id();
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            // 调用内部接口
            result = regManager.isAccountNotExists(params.getUserid(), params.getClient_id());
        } catch (Exception e) {
            logger.error("checkuser:Check User Is Failed,Userid Is " + params.getUserid(), e);
        }
        return result.toString();
    }

}
