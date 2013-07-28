package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.PcTokenParams;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 桌面端采用token+cookie流程的API类
 * User: shipengzhi
 * Date: 13-7-26
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
public class PCAccountController {

    @RequestMapping(value = "/act/getpairtoken")
    @ResponseBody
    public Object getPairToken(PcTokenParams reqParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }


        // 检查client_id和client_secret是否有效


        return result.toString();
    }
}
