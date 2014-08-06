package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.model.account.Account;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
@Component("proxyBindApiManager")
public class ProxyBindApiManagerImpl extends BaseProxyManager implements BindApiManager {

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BIND_EMAIL, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        try {
            String ru = bindEmailApiParams.getRu();
            ru = URLEncoder.encode(ru, "UTF-8");
            bindEmailApiParams.setRu(ru);
        } catch (UnsupportedEncodingException e) {
        }
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
    public Result bindMobile(String passportId, String newMobile, Account account) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.BING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile", newMobile);
        requestModelXml.addParam("userid", passportId);
        return executeResult(requestModelXml);
    }

    @Override
    public Result modifyBindMobile(String passportId, String newMobile) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 解除手机号绑定
     *
     * @param mobile
     * @return
     */
    @Override
    public Result unBindMobile(String mobile) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("mobile", mobile);
        return executeResult(requestModelXml, mobile);
    }


}
