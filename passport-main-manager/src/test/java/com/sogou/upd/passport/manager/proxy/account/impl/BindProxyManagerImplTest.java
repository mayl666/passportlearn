package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.XMLUtil;
import com.sogou.upd.passport.manager.proxy.account.BindProxyManager;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class BindProxyManagerImplTest  extends BaseTest {

    @Inject
    private BindProxyManager bindProxyManager;

    @Test
    public void testBindMobile(){
        BindMobileProxyParams bindMobileProxyParams = new BindMobileProxyParams();
        bindMobileProxyParams.setPassport_id(passportId);
        bindMobileProxyParams.setMobile("18210193340");
        bindMobileProxyParams.setClient_id(clientId);
        Map<String,Object> map= bindProxyManager.bindMobile(bindMobileProxyParams);
        System.out.println(XMLUtil.mapToXml("result", map).asXML());
    }

    @Test
    public void testUnbindMobile(){
        UnBindMobileProxyParams unBindMobileProxyParams=new UnBindMobileProxyParams();
        unBindMobileProxyParams.setMobile("18210193340");
        unBindMobileProxyParams.setClient_id(clientId);
        Map<String,Object> map= bindProxyManager.unbindMobile(unBindMobileProxyParams);
        System.out.println(XMLUtil.mapToXml("result", map).asXML());
    }

    @Test
    public void testBindEmail(){
        BindEmailApiParams bindEmailApiParams=new BindEmailApiParams();
        bindEmailApiParams.setNewbindemail("411541129@qq.com");
        bindEmailApiParams.setOldbindemail("34310327@qq.com");
        bindEmailApiParams.setPassword(password);
        bindEmailApiParams.setClient_id(clientId);
        bindEmailApiParams.setPassport_id(passportId);
        Map<String,Object> map= bindProxyManager.bindEmail(bindEmailApiParams);
        System.out.println(XMLUtil.mapToXml("result", map).asXML());
    }
}
