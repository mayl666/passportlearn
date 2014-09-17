package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.TKeyParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 该类为交换QQ的tKey  参见：http://svn.sogou-inc.com/svn/userplatform/updoc/passport/概要设计方案/2014Q2/搜狗输入法拉取QQ好友/输入法拉取好友列表-概要设计-passport.docx
 * Created by denghua on 14-5-12.
 */
@Controller
@RequestMapping(value = "/connect")
public class ConnectTKeyController extends BaseController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    @ResponseBody
    @RequestMapping(value = "/t_key")
    public String tKey(HttpServletRequest req, TKeyParams tKeyParams) throws Exception {
        Result result = new APIResultSupport(false);
        String userId = null;
        int clientId = tKeyParams.getClient_id();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(tKeyParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            if (!hostHolder.isLogin()) {
                //判断sgid是否存在。如果不存在，则输出未登录信息
                if (tKeyParams.getSgid() == null) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                    return result.toString();
                } else {
                    //判断sgid的正确性。
                    Result r = sessionServerManager.getPassportIdBySgid(tKeyParams.getSgid(), IpLocationUtil.getIp(req));
                    if (r.isSuccess()) {
                        userId = (String) r.getModels().get("passport_id");
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                        return result.toString();
                    }
                }
            } else {
                userId = hostHolder.getPassportId();
            }
            Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId);
            if (!obtainTKeyResult.isSuccess()) {
                return obtainTKeyResult.toString();
            }
            String tKey = (String) obtainTKeyResult.getModels().get("tKey");
            result.setSuccess(true);
            result.getModels().put("tKey", tKey);
            return result.toString();
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
