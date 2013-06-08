package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyRegisterApiManager")
public class ProxyRegisterApiManagerImpl extends BaseProxyManager implements RegisterApiManager {

    @Override
    public Result regMailUser(RegEmailApiParams regEmailApiParams) {
        return null;
    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(regMobileCaptchaApiParams);
        return executeResult(requestModelXml);
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return executeResult(requestModelXml);

    }
}
