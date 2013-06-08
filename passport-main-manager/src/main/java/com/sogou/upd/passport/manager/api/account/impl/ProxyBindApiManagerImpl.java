package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
@Component("proxyBindApiManager")
public class ProxyBindApiManagerImpl extends BaseProxyManager implements BindApiManager {

    private static Logger logger = LoggerFactory.getLogger(ProxyBindApiManagerImpl.class);

    @Override
    public Result bindMobile(BindMobileApiParams bindMobileApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(bindMobileApiParams);
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result unbindMobile(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return this.executeResult(requestModelXml, baseMoblieApiParams.getMobile());
    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_EMAIL, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        bindEmailApiParams.setPwdtype(1);
        requestModelXml.addParams(bindEmailApiParams);
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result getPassportIdFromMobile(MobileBindPassportIdApiParams mobileBindPassportIdApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(mobileBindPassportIdApiParams);
        return this.executeResult(requestModelXml, mobileBindPassportIdApiParams.getMobile());
    }

    @Override
    public Result queryPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.QUERY_MOBILE_BING_ACCOUNT, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return executeResult(requestModelXml);
    }

}
