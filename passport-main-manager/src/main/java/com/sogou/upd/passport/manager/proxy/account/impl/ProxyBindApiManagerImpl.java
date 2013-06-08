package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.BindApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
@Component
public class ProxyBindApiManagerImpl extends BaseProxyManager implements BindApiManager {

    private static Logger logger = LoggerFactory.getLogger(ProxyBindApiManagerImpl.class);

    @Override
    public Result bindMobile(BindMobileProxyParams bindMobileProxyParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(bindMobileProxyParams);
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result unbindMobile(UnBindMobileProxyParams unBindMobileProxyParams) {
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            long ct = System.currentTimeMillis();
            String code = unBindMobileProxyParams.getMobile() + SHPPUrlConstant.APP_ID + SHPPUrlConstant.APP_KEY + ct;
            try {
                code = Coder.encryptMD5(code);
            } catch (Exception e) {
                throw new RuntimeException("calculate code error phone:" + unBindMobileProxyParams.getMobile(), e);
            }
            unBindMobileProxyParams.setCode(code);
            unBindMobileProxyParams.setCt(ct);
            unBindMobileProxyParams.setClient_id(SHPPUrlConstant.APP_ID);
            requestModelXml.addParams(unBindMobileProxyParams);

            return this.executeResult(requestModelXml);
        } catch (Exception e) {
            Result result = new APIResultSupport(false);
            logger.error("unbindMobile Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_EMAIL, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            int pwdType = bindEmailApiParams.getPwdtype();
            if (pwdType == 0) {
                try {
                    String password = Coder.encryptMD5(bindEmailApiParams.getPassword());
                    bindEmailApiParams.setPassword(password);
                } catch (Exception e) {
                    throw new RuntimeException("SecureProxyManagerImpl.bindEmail md5 password error", e);
                }
            }
            requestModelXml.addParams(bindEmailApiParams);
            return this.executeResult(requestModelXml);
        } catch (Exception e) {
            Result result = new APIResultSupport(false);
            logger.error("bindEmail Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result getPassportIdFromMobile(MobileBindPassportIdApiParams mobileBindPassportIdApiParams) {
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            long ct = System.currentTimeMillis();
            String code = mobileBindPassportIdApiParams.getMobile() + SHPPUrlConstant.APP_ID + SHPPUrlConstant.APP_KEY + ct;
            try {
                code = Coder.encryptMD5(code);
            } catch (Exception e) {
                throw new RuntimeException("calculate code error phone:" + mobileBindPassportIdApiParams.getMobile(), e);
            }
            mobileBindPassportIdApiParams.setClient_id(SHPPUrlConstant.APP_ID);
            mobileBindPassportIdApiParams.setCode(code);
            mobileBindPassportIdApiParams.setCt(ct);
            requestModelXml.addParams(mobileBindPassportIdApiParams);
            return this.executeResult(requestModelXml);
        } catch (Exception e) {
            Result result = new APIResultSupport(false);
            logger.error("unbindMobile Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}
