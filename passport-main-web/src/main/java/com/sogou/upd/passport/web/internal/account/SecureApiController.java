package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/security")
public class SecureApiController extends BaseController {

//    @Autowired
//    private ConfigureManager configureManager;
//
//    @Autowired
//    private SecureApiManager proxySecureApiManager;

    @Autowired
    private SecureManager secureManager;

    /**
     * 手机发送短信重置密码
     */
    @RequestMapping(value = "/resetpwd", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @InterfaceSecurity
    public String resetpwd(BaseResetPwdApiParams params) throws Exception {
        Result result = new APIResultSupport(false);

        String lists = params.getLists();
        if (!Strings.isNullOrEmpty(lists)) {
            List<UserNamePwdMappingParams> list = new ObjectMapper().readValue(lists, new TypeReference<List<UserNamePwdMappingParams>>() {
            });
            secureManager.resetPwd(list);
        }
        result.setSuccess(true);
        result.setMessage("获取成功");

        return result.toString();
    }
//
//    /**
//     * 根据userId获取用户安全信息
//     *
//     * @param request
//     * @param params
//     * @return
//     */
//    @RequestMapping(value = "/info", method = RequestMethod.POST)
//    @ResponseBody
//    public Object regMobileCaptchaUser(HttpServletRequest request, GetSecureInfoApiParams params){
//        Result result = new APIResultSupport(false);
//        // 参数校验
//        String validateResult = ControllerHelper.validateParams(params);
//        if (!Strings.isNullOrEmpty(validateResult)) {
//            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//            result.setMessage(validateResult);
//            return result.toString();
//        }
//        // 签名和时间戳校验
//        result = configureManager.verifyInternalRequest(params.getUserid(), params.getClient_id(), params.getCt(), params.getCode());
//        if (!result.isSuccess()) {
//            result.setCode(ErrorUtil.);
//            return result.toString();
//        }
//        // 调用内部接口
//        result = proxySecureApiManager.getUserSecureInfo(params);
//        return result.toString();
//    }

}
