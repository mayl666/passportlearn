package com.sogou.upd.passport.web.account.action.mapp;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.mapp.MappGetUserinfoParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 14-12-26
 * Time: 下午3:43
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/mapp/userinfo")
public class MappUserinfoAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MappUserinfoAction.class);

    @Autowired
    private SessionServerManager sessionServerManager;

    @Autowired
    private CheckManager checkManager;

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;

    @RequestMapping(value = "/getuserinfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getRealtimeUserinfo(HttpServletRequest request, MappGetUserinfoParams params)throws Exception{

        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String ip = getIp(request);
        String sgid=params.getSgid();
        String udid = "";
        String passportId="";

        try{
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            //解析cinfo信息
            TerminalAttribute attributeDO = new TerminalAttribute(request);
            udid = attributeDO.getUdid();
            //验证code是否有效
            boolean isVaildCode = checkManager.checkMappCode(udid, clientId, params.getCt(), params.getCode());
            if (!isVaildCode) {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
                return result.toString();
            }

            //校验sgid，获取passport_id
            Result verifySidResult = sessionServerManager.getPassportIdBySgid(sgid, ip);
            if (verifySidResult.isSuccess()) {
                passportId = (String) verifySidResult.getModels().get("passport_id");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }

            //查询用户信息
            if(Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }
            GetUserInfoApiparams getUserinfoParams=buildMappGetUserInfoApiparams(params,passportId);
            result = sgUserInfoApiManager.getUserInfo(getUserinfoParams);
    
            processAvatarUrl(request, result);
            removeUseless(result);

        }catch (Exception e){
            logger.error("mapp get realtime userinfo eror," + "udid:" + udid);
            result.setSuccess(false);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }finally {
            //记录useroperation
            UserOperationLog userOperationLog = new UserOperationLog(udid, String.valueOf(clientId), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }

        //返回结果
//        logger.warn("app get user realtime info:"+result.toString());
        return result.toString();

    }

    private GetUserInfoApiparams buildMappGetUserInfoApiparams(MappGetUserinfoParams params,String passportId) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        //设置默认fields
        String defaultFields="uniqname,gender,avatarurl,uid";
        String fields = (Strings.isNullOrEmpty(params.getFields())) ? (defaultFields):(defaultFields+","+params.getFields());
        infoApiparams.setFields(fields);
        infoApiparams.setImagesize("30,50,180");
        infoApiparams.setUserid(passportId);
        infoApiparams.setClient_id(params.getClient_id());

        return infoApiparams;
    }

    //除去请求中多余的field
    private void removeUseless(Result result) {
        if (result.isSuccess()) {
            result.getModels().remove("avatarurl");
        }
    }
}
