package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.BindProxyManager;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
@Component
public class BindProxyManagerImpl extends BaseProxyManager implements BindProxyManager {

    @Override
    public Map<String, Object> bindMobile(BindMobileProxyParams bindMobileProxyParams) {
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.BING_MOBILE,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(bindMobileProxyParams);
        return this.execute(requestModelXml);
    }

    @Override
    public Map<String, Object> unbindMobile(UnBindMobileProxyParams unBindMobileProxyParams){
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        long ct=System.currentTimeMillis();
        String code= unBindMobileProxyParams.getMobile()+SHPPUrlConstant.APP_ID+SHPPUrlConstant.APP_KEY+ct;
        try {
            code= Coder.encryptMD5(code);

        } catch (Exception e) {
            throw new RuntimeException("calculate code error phone:"+ unBindMobileProxyParams.getMobile(),e);
        }
        unBindMobileProxyParams.setCode(code);
        unBindMobileProxyParams.setCt(ct);
        requestModelXml.addParams(unBindMobileProxyParams);
        return this.execute(requestModelXml);
    }

    @Override
    public Map<String, Object> bindEmail(BindEmailApiParams bindEmailApiParams){
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.BIND_EMAIL,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        int pwdType=bindEmailApiParams.getPwdtype();
        if(pwdType==0){
            try {
                String password=Coder.encryptMD5(bindEmailApiParams.getPassword());
                bindEmailApiParams.setPassword(password);
            } catch (Exception e) {
                throw new RuntimeException("SecureProxyManagerImpl.bindEmail md5 password error",e);
            }
        }
        requestModelXml.addParams(bindEmailApiParams);
        return this.execute(requestModelXml);
    }

}
