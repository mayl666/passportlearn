package com.sogou.upd.passport.web.internal.connect.info;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.InfoOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.info.InfoOpenApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.internal.connect.OpenApiParamsHelper;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:30
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect/info")
public class InfoOpenApiController {

    @Autowired
    private InfoOpenApiManager proxyInfoOpenApiManager;

    /**
     * 第三方发图片微博，或者发图片分享
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/share/add_pic", method = RequestMethod.POST)
    @ResponseBody
    public Object addUserShareOrPic(InfoOpenApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        BaseOpenApiParams baseOpenApiParams = new OpenApiParamsHelper().createBaseForm(params);
        result = proxyInfoOpenApiManager.addUserShareOrPic(baseOpenApiParams);
        return result.toString();
    }

}
