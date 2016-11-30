package com.sogou.upd.passport.web.internal.connect.info;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.InfoOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.info.InfoOpenApiParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.internal.connect.OpenApiParamsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:30
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect/info")
public class InfoOpenApiController extends BaseController {

//    private static Logger logger = LoggerFactory.getLogger(InfoOpenApiController.class);
//
//    @Autowired
//    private InfoOpenApiManager proxyInfoOpenApiManager;
//
//    /**
//     * 第三方发图片微博，或者发图片分享
//     *
//     * @param params
//     * @return
//     */
//    @InterfaceSecurity
//    @RequestMapping(value = "/share/add_pic", method = RequestMethod.POST)
//    @ResponseBody
//    public Object addUserShareOrPic(InfoOpenApiParams params, HttpServletRequest request) {
//        Result result = new APIResultSupport(false);
//        try {
//            // 参数校验
//            String validateResult = ControllerHelper.validateParams(params);
//            if (!Strings.isNullOrEmpty(validateResult)) {
//                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//                result.setMessage(validateResult);
//                return result.toString();
//            }
//            BaseOpenApiParams baseOpenApiParams = new OpenApiParamsHelper().createBaseForm(params);
//            result = proxyInfoOpenApiManager.addUserShareOrPic(baseOpenApiParams);
//        } catch (Exception e) {
//            logger.error("addUserShareOrPic:Add User Share Picture For Internal Is Failed,Userid is " + params.getUserid(), e);
//        } finally {
//            //记录log
//            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), params.getUser_ip());
//            UserOperationLogUtil.log(userOperationLog);
//        }
//
//        return result.toString();
//    }

}
