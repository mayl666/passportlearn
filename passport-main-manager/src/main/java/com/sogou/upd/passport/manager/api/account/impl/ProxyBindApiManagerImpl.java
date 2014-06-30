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
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
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

    /**
     * 检查用户是否绑定了mobile
     * @param userid
     * @param mobile
     * @return
     */
    private boolean checkBind(String userid, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        Result result = this.getPassportIdByMobile(baseMoblieApiParams);
        if (result.isSuccess() && result.getModels().containsKey("userid")) {
            String bindUserId = result.getModels().get("userid").toString();
            if (bindUserId.trim().equals(userid.trim())) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * 解除绑定
//     *
//     * @param mobile 手机号
//     * @return
//     */
//    private Result unBindMobile(String mobile) {
//        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
//        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
//        baseMoblieApiParams.setMobile(mobile);
//        requestModelXml.addParams(baseMoblieApiParams);
//        return this.executeResult(requestModelXml, baseMoblieApiParams.getMobile());
////    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_EMAIL, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        bindEmailApiParams.setPwdtype(1);
        requestModelXml.addParams(bindEmailApiParams);
        requestModelXml.addHeader("Accept-Language", "zh-CN");
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return executeResult(requestModelXml, baseMoblieApiParams.getMobile());
    }

    @Override
    public Result sendCaptcha(SendCaptchaApiParams sendCaptchaApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.SEND_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(sendCaptchaApiParams);
        return executeResult(requestModelXml, sendCaptchaApiParams.getMobile());
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

    @Override
    public Result bindMobile(String passportId,String newMobile){
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile",newMobile);
        requestModelXml.addParam("userid",passportId);
        return executeResult(requestModelXml);
    }

    /**
     * 解除手机号绑定
     * @param mobile
     * @return
     */
    @Override
    public Result unBindMobile(String mobile){
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile",mobile);
        return executeResult(requestModelXml,mobile);
    }


}
