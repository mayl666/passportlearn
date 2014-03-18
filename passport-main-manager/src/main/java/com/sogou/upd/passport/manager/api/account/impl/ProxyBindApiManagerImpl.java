package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.SendCaptchaApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
@Component("proxyBindApiManager")
public class ProxyBindApiManagerImpl extends BaseProxyManager implements BindApiManager {

    private static Logger logger = LoggerFactory.getLogger(ProxyBindApiManagerImpl.class);

    @Qualifier("redisUtils")
    @Autowired
    private RedisUtils redisUtils;

    private static String CACHE_PREFIX_MOBILE_SMSCODE_PROXY = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE_PROXY;

//    @Override
//    public Result bindMobile(BindMobileApiParams bindMobileApiParams) {
//        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
//        requestModelXml.addParams(bindMobileApiParams);
//        return this.executeResult(requestModelXml);
//    }
//
//    @Override
//    public Result updateBindMobile(UpdateBindMobileApiParams updateBindMobileApiParams) {
//        Result result = new APIResultSupport(false);
//
//        //检查新手机号是否已经绑定了其他用户
//        BaseMobileApiParams baseMoblieApiParams = new BaseMobileApiParams();
//        baseMoblieApiParams.setMobile(updateBindMobileApiParams.getNewMobile());
//        Result resultQuery = this.getPassportIdByMobile(baseMoblieApiParams);
//        switch(resultQuery.getCode()){
//            //新手机已经绑定了其他用户
//            case "0":
//                String oldUserId = resultQuery.getModels().get("userid").toString();
//                if(oldUserId.trim().equals(updateBindMobileApiParams.getUserid().trim())){
//                    result.setSuccess(true);
//                    result.setMessage("用户目前已经绑定了新手机号");
//                }else{
//                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
//                    result.setMessage("新手机已经绑定其他用户");
//                }
//                return result;
//            //新手机为绑定其他用户
//            case ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND:
//                break;
//            //其他情况直接返回
//            default:
//                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
//                result.setMessage("绑定失败");
//                return result;
//        }
//
//        //检查绑定关系
//        if (!this.checkBind(updateBindMobileApiParams.getUserid(), updateBindMobileApiParams.getOldMobile())) {
//            result.setMessage("原手机与用户绑定关系检查失败");
//        }
//
//
//        //解除老的绑定的关系
//        result= this.unBindMobile(updateBindMobileApiParams.getOldMobile());
//        if(!result.isSuccess()){
//            return result;
//        }
//
//
//        //建立新的绑定关系
//        BindMobileApiParams bindMobileApiParams=new BindMobileApiParams();
//        bindMobileApiParams.setMobile(updateBindMobileApiParams.getNewMobile());
//        bindMobileApiParams.setUserid(updateBindMobileApiParams.getUserid());
//        result = this.bindMobile(bindMobileApiParams);
//
//
//        //如果绑定新手机失败了，将老手机再绑回去，做一个回滚
//        if(!result.isSuccess()){
//            BindMobileApiParams bindMobileApiParamsOld=new BindMobileApiParams();
//            bindMobileApiParamsOld.setMobile(updateBindMobileApiParams.getOldMobile());
//            bindMobileApiParamsOld.setUserid(updateBindMobileApiParams.getUserid());
//            this.bindMobile(bindMobileApiParamsOld);
//        }
//        return result;
//    }
//
//    /**
//     * 检查用户是否绑定了mobile
//     * @param userid
//     * @param mobile
//     * @return
//     */
//    private boolean checkBind(String userid, String mobile) {
//        BaseMobileApiParams baseMoblieApiParams = new BaseMobileApiParams();
//        baseMoblieApiParams.setMobile(mobile);
//        Result result = this.getPassportIdByMobile(baseMoblieApiParams);
//        if (result.isSuccess() && result.getModels().containsKey("userid")) {
//            String bindUserId = result.getModels().get("userid").toString();
//            if (bindUserId.trim().equals(userid.trim())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 解除绑定
//     *
//     * @param mobile 手机号
//     * @return
//     */
//    private Result unBindMobile(String mobile) {
//        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
//        BaseMobileApiParams baseMoblieApiParams = new BaseMobileApiParams();
//        baseMoblieApiParams.setMobile(mobile);
//        requestModelXml.addParams(baseMoblieApiParams);
//        return this.executeResult(requestModelXml, baseMoblieApiParams.getMobile());
//    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_EMAIL, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        bindEmailApiParams.setPwdtype(1);
        requestModelXml.addParams(bindEmailApiParams);
        requestModelXml.addHeader("Accept-Language", "zh-CN");
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result getPassportIdByMobile(BaseMobileApiParams baseMobileApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMobileApiParams);
        return executeResult(requestModelXml, baseMobileApiParams.getMobile());
    }

    @Override
    public Result sendCaptcha(SendCaptchaApiParams sendCaptchaApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.SEND_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(sendCaptchaApiParams);
        return executeResult(requestModelXml, sendCaptchaApiParams.getMobile());
    }

    @Override
    public Result bindMobile(BindMobileApiParams bindMobileApiParams){
        String oldMobile=bindMobileApiParams.getOldMobile();
        String oldCaptcha=bindMobileApiParams.getOldCaptcha();
        if(!StringUtil.isBlank(oldMobile)&&!StringUtil.isBlank(oldCaptcha)){
             Result result= this.unbindMobileByCaptcha(oldMobile,oldCaptcha);
            if(!result.isSuccess()){
                if (ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE.equals(result.getCode())) {
                    result.setMessage("原密保手机验证码错误或已过期");
                }
               return result;
            }
        }
        String userid=bindMobileApiParams.getUserid();
        String newMoblie=bindMobileApiParams.getNewMobile();
        String newCaptcha=bindMobileApiParams.getNewCaptcha();
        Result result=this.bindMobileByCaptcha(userid,newMoblie,newCaptcha);
        if(!StringUtil.isBlank(oldMobile)&&!result.isSuccess()){
            StringBuilder errorLog=new StringBuilder("BindNewMobileField: userid=");
            errorLog.append(userid);
            errorLog.append(" , oldMobile=");
            errorLog.append(oldMobile);
            errorLog.append(" ,newMoblie=");
            errorLog.append(newMoblie);
            errorLog.append(" ,result=");
            errorLog.append(result.toString());
            logger.error(errorLog.toString());
        }
        if (ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE.equals(result.getCode())) {
            result.setMessage("新手机验证码错误或已过期");
        }
        return result;
    }

    @Override
    public boolean cacheOldCaptcha(String mobile, int clientId, String captcha) throws ServiceException{
        String cacheKey = buildOldCaptchaKey(mobile, clientId);
        try {
            redisUtils.setWithinSeconds(cacheKey, captcha, SMSUtil.SMS_VALID);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public String getOldCaptcha(String mobile, int clientId) throws ServiceException {
        String cacheKey = buildOldCaptchaKey(mobile, clientId);
        try {
            String captcha = redisUtils.get(cacheKey);
            return captcha;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildOldCaptchaKey(String mobile, int clientId) {
        return CACHE_PREFIX_MOBILE_SMSCODE_PROXY + mobile + "_" + clientId;
    }

    /**
     * 通过验证码解绑手机号
     * @param mobile
     * @param captcha
     * @return
     */
    private Result unbindMobileByCaptcha(String mobile,String captcha){
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBIND_MOBILE_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile",mobile);
        requestModelXml.addParam("captcha",captcha);
        return executeResult(requestModelXml, mobile);
    }

    /**
     * 通过验证码绑定手机号
     * @param userid
     * @param mobile
     * @param captcha
     * @return
     */
    private Result bindMobileByCaptcha(String userid,String mobile,String captcha){
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_MOBILE_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile",mobile);
        requestModelXml.addParam("captcha",captcha);
        requestModelXml.addParam("userid",userid);
        return executeResult(requestModelXml);
    }

}
