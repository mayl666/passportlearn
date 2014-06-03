package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
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
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;

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
        //第三方获取个人资料
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(params.getUserid());
        // 调用内部接口
        if (domain == AccountDomainEnum.THIRD) {
            result = sgUserInfoApiManager.getUserInfo(params);
        } else {
            result = proxyUserInfoApiManager.getUserInfo(params);
        }
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
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(params.getUserid());

        // TODO 禁止修改昵称
        if (params.getUniqname() != null) {
            result.setCode(ErrorUtil.FORBID_UPDATE_USERINFO);
        } else {
            // 调用内部接口
            if (domain == AccountDomainEnum.THIRD) {
                result = sgUserInfoApiManager.updateUserInfo(params);
            } else {
                result = proxyUserInfoApiManager.updateUserInfo(params);
            }
        }

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
        result = proxyUserInfoApiManager.checkUniqName(params);
        UserOperationLog userOperationLog = new UserOperationLog(params.getUniqname(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

}
