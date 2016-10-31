package com.sogou.upd.passport.web.session;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.session.form.VerifySgidParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * session server 公网接口
 * @author wanghuaqing
 */
@Controller
@RequestMapping("/session")
public class SessionApiController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(SessionApiController.class);
  
  @Autowired
  private SessionServerManager sessionServerManager;
  @Autowired
  private ConfigureManager configureManager;
  @Autowired
  private CheckManager checkManager;
  
  /**
   * sgid校验接口
   *
   * @param request
   * @param params
   * @return
   */
  @RequestMapping(value = "/verify_sid")
  @ResponseBody
  public String verifySid(HttpServletRequest request, VerifySgidParam params) {
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
  
    String udid = "";
    String ip = getIp(request);
  
    //解析cinfo信息
    TerminalAttribute terminalAttribute = null;
    try {
      terminalAttribute = new TerminalAttribute(request);
      udid = terminalAttribute.getUdid();
    } catch (ServiceException e) {
      udid = "";
    }
  
    //验证code是否有效
    boolean isVaildCode = checkManager.checkMappCode(udid, clientId, params.getCt(), params.getCode());
    if (!isVaildCode) {
      result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
      return result.toString();
    }
    
    try {
      // 调用 session server 校验 sgid
      result = sessionServerManager.verifySid(params.getSgid(), clientId, ip);
    } catch (Exception e) {
      logger.error("/session/verify_sid failed, sgid:" + params.getSgid(), e);
      result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
      return result.toString();
    }
    
    return result.toString();
  }
  
}
