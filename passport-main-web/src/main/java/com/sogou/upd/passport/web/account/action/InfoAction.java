package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.SlowInfoParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by hujunfei Date: 14-4-15 Time: 上午11:41
 */
@Controller
@RequestMapping("/web")
public class InfoAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(InfoAction.class);

    @RequestMapping(value = "/slowinfo", method = RequestMethod.GET)
    public void slowInfo(HttpServletRequest request, SlowInfoParams params) throws Exception {
        Result result = new APIResultSupport(true);

        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return;
            /*result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();*/
        }

        UserOperationLog log = new UserOperationLog("-", request.getRequestURI(), "-", result.getCode(), getIp(request));
        Map map = BeanUtil.objectToMap(params);
        log.setOtherMessageMap(map);

        UserOperationLogUtil.log(log);
    }
}
