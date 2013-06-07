package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.SecureProxyManager;
import com.sogou.upd.passport.manager.proxy.account.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:26
 */
@Component
public class SecureProxyManagerImpl extends BaseProxyManager implements SecureProxyManager {

    private static Logger logger = LoggerFactory.getLogger(SecureProxyManagerImpl.class);

    @Override
    public Result updatePwd(UpdatePwdApiParams updatePwdApiParams) {
        Result result = new APIResultSupport(false);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_PWD, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(updatePwdApiParams);
            return this.executeResult(requestModelXml);
        } catch (Exception e) {
            logger.error(updatePwdApiParams.getPassport_id()+" updatePwd Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) {
        Result result = new APIResultSupport(false);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_PWD, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(updateQuesApiParams);
            return this.executeResult(requestModelXml);
        } catch (Exception e) {
            logger.error(updateQuesApiParams.getPassport_id()+" updateQues  Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }
}
