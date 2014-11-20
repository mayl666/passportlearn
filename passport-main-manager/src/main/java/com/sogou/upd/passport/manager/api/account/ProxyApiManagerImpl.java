package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;

/**
 * 搜狐遗漏的接口
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public class ProxyApiManagerImpl extends BaseProxyManager {

    /**
     * 搜狐接口，根据手机号获取主账号
     * @param baseMoblieApiParams
     * @return
     */
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return executeResult(requestModelXml, baseMoblieApiParams.getMobile());
    }

    /**
     * 搜狐接口，检查账号是否存在
     * @param checkUserApiParams
     * @return
     */
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.CHECK_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(checkUserApiParams);
        Result result = executeResult(requestModelXml);
        if (!result.isSuccess()) {
            result.setDefaultModel("userid", checkUserApiParams.getUserid());
        }
        return result;
    }

}
