package com.sogou.upd.passport.web.internal.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.manager.api.CacheSyncUpdateManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
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
public class CacheTestUnitController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CacheTestUnitController.class);
    @Autowired
    private CacheSyncUpdateManager cacheSyncUpdateManager;

    @InterfaceSecurity
    @RequestMapping(value = "/internal/debug/testunit_cache", method = RequestMethod.POST)
    @ResponseBody
    public String cacheSync(HttpServletRequest req, CacheTestUnitParams params) throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String cacheKey = params.getKey();
        String cacheType = params.getType();
        String cacheOper = params.getOper();
        String ip = getIp(req);
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, req)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            result = cacheSyncUpdateManager.readOperCache(cacheKey, cacheType, cacheOper);
            return result.toString();
        } catch (Exception e) {
            log.error("calculate default code error", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            // 获取记录UserOperationLog的数据
            UserOperationLog userOperationLog = new UserOperationLog("", String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("key", cacheKey);
            userOperationLog.putOtherMessage("type", cacheType);
            userOperationLog.putOtherMessage("oper",cacheOper);
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
