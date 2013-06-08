package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:26
 */
@Component
public class ProxySecureApiManagerImpl extends BaseProxyManager implements SecureApiManager {

    private static Logger logger = LoggerFactory.getLogger(ProxySecureApiManagerImpl.class);

    @Override
    public Result updatePwd(UpdatePwdApiParams updatePwdApiParams) {
        Result result = new APIResultSupport(false);
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_PWD, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(updatePwdApiParams);

        return this.executeResult(requestModelXml);

    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_PWD, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(updateQuesApiParams);
        return this.executeResult(requestModelXml);
    }
}
