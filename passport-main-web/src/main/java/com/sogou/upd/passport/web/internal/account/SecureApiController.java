package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.ModuleBlackListParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.manager.moduleblacklist.ModuleBlackListManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/security")
public class SecureApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SecureApiController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private ConfigureManager configureManager;

    @Autowired
    private ModuleBlackListManager moduleBlackListManager;

    @Autowired
    private RedisUtils redisUtils;

    //黑名单用户列表分隔符
    private static final String BLACK_USER_LIST_VALUE_SPLIT = "\r\n";

    //返回给module结果集中userid与时间戳的分隔符
    private static final String BLACK_USER_EXPIRETIME_SPLIT = " ";

    //redis中保存黑名单userid与时间戳分隔符
    private static final String BLACK_USER_EXPIRETIME_REDIS_SPLIT = "\\^";

    //有效期
    private static final int EXPIRE_TIME = 60;

    /**
     * 手机发送短信重置密码
     */
    @RequestMapping(value = "/resetpwd_batch", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String resetpwd(HttpServletRequest request, BaseResetPwdApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String lists = params.getLists();
        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, request)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            if (!Strings.isNullOrEmpty(lists)) {
                List<UserNamePwdMappingParams> list = new ObjectMapper().readValue(lists, new TypeReference<List<UserNamePwdMappingParams>>() {
                });
                result = secureManager.resetPwd(list, clientId);
            } else {
                result.setSuccess(true);
                result.setMessage("lists为空");
            }
            return result.toString();
        } catch (Exception e) {
            log.error("Batch resetpwd fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("lists", lists);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /**
     * module黑名单接口
     * <p/>
     * 数据格式 数据全量(增量)，获取增量数据的偏移标志位  黑名单接口调用间隔(单位秒)
     *
     * @param request
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/moduleblacklist", method = RequestMethod.GET)
    @ResponseBody
    public String moduleBlackList(HttpServletRequest request, ModuleBlackListParams params) throws Exception {
        Result result = new APIResultSupport(false);

        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                return result.toString();
            }
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }


            //有效期 （当前时间+60秒）秒
//            long expireTime = (System.currentTimeMillis() / 1000) + EXPIRE_TIME;

            //获取Redis中保存的黑名单数据
            Set<String> set = redisUtils.smember(CacheConstant.CACHE_KEY_BLACKLIST);

//            if (params.getIs_delta() != 0 || params.getIs_delta() != 1) {
//                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//                return result.toString();
//            }

            //接口调用频率
//            int update_internal;
//            if (params.getUpdate_interval() == 0) {
//                update_internal = 10;
//            } else {
//                update_internal = params.getUpdate_interval();
//            }

            StringBuffer resultText = new StringBuffer("0 0 3");
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String str = it.next();
                String strs[] = str.split(BLACK_USER_EXPIRETIME_REDIS_SPLIT);
                resultText.append(BLACK_USER_LIST_VALUE_SPLIT).append(strs[0]).append(BLACK_USER_EXPIRETIME_SPLIT).append(strs[1]);
            }

            result.setSuccess(true);
            return resultText.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog("", String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(request));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
