package com.sogou.upd.passport.web.internal.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.CacheSyncUpdateManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 数据同步中缓存更新方法
 * User: shipengzhi
 * Date: 14-6-3
 * Time: 下午7:02
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CacheSyncUpdateController extends BaseController {

    private static final long API_REQUEST_VAILD_TERM = 50 * 61 * 1000; //接口请求的有效期为5分钟，单位为毫秒

    private static final Logger log = LoggerFactory.getLogger(CacheSyncUpdateController.class);

    @Autowired
    private CacheSyncUpdateManager cacheSyncUpdateManager;

    @RequestMapping(value = "/internal/debug/cachesync", method = RequestMethod.GET)
    @ResponseBody
    public String cacheSync(HttpServletRequest req, CacheSyncParam params) throws Exception {
        Result result = new APIResultSupport(false);
        String key = params.getKey();
        long ts = params.getTs();
        String originalCode = params.getCode();
        String ip = getIp(req);
        String secret = "c3%uH@FSOIkeopP23#wk_hUj7^?\"OP";
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //签名校验
            String secretStr = key + ts + secret;
            String code = Coder.encryptMD5(secretStr);
            long currentTime = System.currentTimeMillis();
            if (!code.equalsIgnoreCase(originalCode) || ts < currentTime - API_REQUEST_VAILD_TERM) {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
                return result.toString();
            }
            //IP白名单
            if (ip.equals("10.146.16.142")) {
                result = cacheSyncUpdateManager.deleteTableCache(key);
            } else {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
            }
            return result.toString();
        } catch (Exception e) {
            log.error("calculate default code error", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            // 获取记录UserOperationLog的数据
            UserOperationLog userOperationLog = new UserOperationLog(key, "1120", result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
