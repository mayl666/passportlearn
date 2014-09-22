package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.manager.api.account.form.RSAApiParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 通过rsa加密的方式来获取相关信息。
 * <p>
 * 以rsa当做验证状态。 加密者使用公钥加密。 passport使用私钥解密，得到相关信息。
 * </p>
 * Created by denghua on 14-6-10.
 */
@Controller
@RequestMapping("/internal/rsa")
public class RSAApiController extends BaseController {

    @Autowired
    private AccountRoamManager accountRoamManager;

    @InterfaceSecurity
    @RequestMapping(value = "/userid", method = RequestMethod.POST)
    @ResponseBody
    public String getUserId(HttpServletRequest request, RSAApiParams params) {

        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            String userId = accountRoamManager.getUserIdByPinyinRoamToken(params.getCipherText());
            if (!Strings.isNullOrEmpty(userId)) {
                result.setMessage("操作成功");
                result.getModels().put("userid", userId);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_RSA_DECRYPT);
                return result.toString();
            }
        } finally {
            String userId = StringUtils.defaultString(String.valueOf(result.getModels().get("userId")));
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }
}