package com.sogou.upd.passport.web.internal.sync;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.config.form.AppAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.AppUpdateSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdUpdateSyncApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.sync.SyncManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 同步内部接口 <br />
 * 开放平台同步 app config、connect config 等信息
 */
@Controller
@RequestMapping("/internal/sync")
public class SyncApiController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SyncApiController.class);
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SyncManager syncManager;

    /**
     * 同步添加 app
     */
    @InterfaceSecurity
    @RequestMapping(value = "/add_app", method = RequestMethod.POST)
    @ResponseBody
    public Object addApp(AppAddSyncApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        // 验证client_id是否存在
        int clientId = params.getClient_id();
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = syncManager.addApp(params);

        logger.info("sync add app result:" + result);

        return result.toString();
    }

    /**
     * 同步修改 app
     */
    @InterfaceSecurity
    @RequestMapping(value = "/update_app", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateApp(AppUpdateSyncApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        // 验证client_id是否存在
        int clientId = params.getClient_id();
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = syncManager.updateApp(params);

        logger.info("sync update app result:" + result);

        return result.toString();
    }

    /**
     * 同步删除 app
     */
    @InterfaceSecurity
    @RequestMapping(value = "/del_app", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteApp(AppUpdateSyncApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        if(params.getAppId() < 10000) {  // 判断相当信息是否正确
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            return result;
        }

        // 验证client_id是否存在
        int clientId = params.getClient_id();
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = syncManager.deleteApp(params.getAppId());

        logger.info("sync delete app result:" + result);

        return result.toString();
    }

    /**
     * 同步添加第三方
     */
    @InterfaceSecurity
    @RequestMapping(value = "/add_third", method = RequestMethod.POST)
    @ResponseBody
    public Object addThird(ThirdAddSyncApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        //验证client_id是否存在
        int clientId = params.getClient_id();
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = syncManager.addThird(params);

        logger.info("sync add third result:" + result);

        return result.toString();
    }

    /**
     * 同步删除第三方
     */
    @InterfaceSecurity
    @RequestMapping(value = "/del_third", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteApp(ThirdUpdateSyncApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        if(params.getAppId() < 10000) {  // 判断相当信息是否正确
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            return result;
        }

        //验证client_id是否存在
        int clientId = params.getClient_id();
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = syncManager.deleteThird(params);

        logger.info("sync delete third result:" + result);

        return result.toString();
    }
}
