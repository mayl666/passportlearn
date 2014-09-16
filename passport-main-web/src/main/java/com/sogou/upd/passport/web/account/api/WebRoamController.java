package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.BaseWebRuParams;
import com.sogou.upd.passport.web.account.form.PcRoamGoParams;
import com.sogou.upd.passport.web.account.form.WebRoamParams;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 支持web端搜狗域、搜狐域、第三方账号漫游
 * User: chengang
 * Date: 14-7-29
 * Time: 下午4:28
 */
@Controller
@RequestMapping("/sso")
public class WebRoamController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRoamController.class);
    private static final String SG_WEB_ROAM_URL = CommonConstant.DEFAULT_INDEX_URL + "/sso/web_roam";

    @Autowired
    private AccountRoamManager accountRoamManager;
    @Autowired
    private HostHolder hostHolder;

    /*
     * 解析搜狐侧登录态，并将登录态传递给搜狗侧,
     * 目前支持搜狐漫游到搜狗，sg.passport.sohu.com
     */
    @ResponseBody
    @RequestMapping(value = "/web_roam_go", method = RequestMethod.GET)
    public void webRoamGo(HttpServletRequest request, HttpServletResponse response, BaseWebRuParams baseWebRuParams) throws Exception {
        Result result = new APIResultSupport(false);
        String clientId = baseWebRuParams.getClient_id();
        String ru = baseWebRuParams.getRu();
        String ip = getIp(request);
        //判断是否登录
        String sLoginPassportId;
        if (hostHolder.isLogin()) {
            sLoginPassportId = hostHolder.getPassportId();
        } else {
            response.sendRedirect(ru);
            return;
        }
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(baseWebRuParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                returnErrMsg(response, ru, result.getCode(), result.getMessage());
                return;
            }
            result = accountRoamManager.createRoamKey(sLoginPassportId);
            if (result.isSuccess()) {
                String r_key = (String) result.getModels().get("r_key");
                Map params = Maps.newHashMap();
                params.put("client_id", clientId);
                params.put("r_key", r_key);
                params.put("ru", ru);
                response.sendRedirect(ServletUtil.applyOAuthParametersString(SG_WEB_ROAM_URL, params));
                return;
            } else {
                returnErrMsg(response, ru, result.getCode(), result.getMessage());
                return;
            }
        } catch (Exception e) {
            LOGGER.error("web_roam_go error.shUserId:{},clientId:{},ru:{}", new Object[]{sLoginPassportId, clientId, ru}, e);
        } finally {
            //记录用户操作日志
            UserOperationLog userOperationLog = new UserOperationLog(sLoginPassportId, request.getRequestURI(), clientId, result.getCode(), ip);
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 验证搜狐侧登录态生成的r_key，验证通过则在搜狗侧种登录态,
     */
    @ResponseBody
    @RequestMapping(value = "/web_roam", method = RequestMethod.GET)
    public void webRoam(HttpServletRequest request, HttpServletResponse response, WebRoamParams webRoamParams) throws Exception {
        Result result = new APIResultSupport(false);
        String ru = webRoamParams.getRu();
        String r_key = webRoamParams.getR_key();
        String clientId = webRoamParams.getClient_id();
        String createIp = getIp(request);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(webRoamParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                returnErrMsg(response, ru, result.getCode(), result.getMessage());
                return;
            }

            String sgLgUserId = StringUtils.EMPTY;
            if (hostHolder.isLogin()) {
                sgLgUserId = hostHolder.getPassportId();
            }
            result = accountRoamManager.webRoam(response, sgLgUserId, r_key, ru, createIp, Integer.parseInt(clientId));
            if (result.isSuccess()) {
                response.sendRedirect(ru);
                return;
            } else {
                returnErrMsg(response, ru, result.getCode(), result.getMessage());
                return;
            }
        } catch (Exception e) {
            LOGGER.error(" web_roam error.userId:{},r_key:{},ru", new Object[]{result.getModels().get("userId"), r_key, ru}, e);
        } finally {
            String resultCode = StringUtils.defaultIfEmpty(result.getCode(), "0");
            String userId = StringUtils.defaultString(String.valueOf(result.getModels().get("userId")));

            //记录用户操作日志
            UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), clientId, resultCode, createIp);
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 桌面端产品登录态透传到web端的起始接口
     * 验证桌面端登录态，并生成已登录标识
     */
    @RequestMapping(value = "/pc_roam_go", method = RequestMethod.POST)
    public String pcRoamGo(HttpServletRequest request, HttpServletResponse response, Model model, PcRoamGoParams pcRoamGoParams) throws Exception {
        Result result = new APIResultSupport(false);
        String clientId = pcRoamGoParams.getClient_id();
        String xd = pcRoamGoParams.getXd();
        String createIp = getIp(request);
        String userId = "";
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(pcRoamGoParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result.setDefaultModel("xd", xd);
                model.addAttribute("data", result.toString());
                return "/login/roam";
            }

            result = accountRoamManager.pcRoamGo(pcRoamGoParams.getType(), pcRoamGoParams.getS());
            if (result.isSuccess()) {
                result.setDefaultModel("r_key", result.getModels().get("r_key"));
                String uniqname = Coder.encode((String) result.getModels().get("uniqname"), CommonConstant.DEFAULT_CHARSET);
                result.setDefaultModel("uniqname", uniqname);
                userId = (String) result.getModels().get("userId");
            }
            result.setDefaultModel("xd", xd);
            model.addAttribute("data", result.toString());
            return "/login/roam";
        } finally {
            //记录用户操作日志
            UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), clientId, result.getCode(), createIp);
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
