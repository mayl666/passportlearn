package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.SecureProxyManager;
import com.sogou.upd.passport.manager.proxy.account.form.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:26
 */
@Component
public class SecureProxyManagerImpl extends BaseProxyManager implements SecureProxyManager{

    @Override
    public Map<String, Object> updatePwd(UpdatePwdApiParams updatePwdApiParams){
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.UPDATE_PWD,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(updatePwdApiParams);
        return this.execute(requestModelXml);
    }

    @Override
    public Map<String, Object> updateQues(UpdateQuesApiParams updateQuesApiParams){
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.UPDATE_PWD,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(updateQuesApiParams);
        return this.execute(requestModelXml);
    }
}
