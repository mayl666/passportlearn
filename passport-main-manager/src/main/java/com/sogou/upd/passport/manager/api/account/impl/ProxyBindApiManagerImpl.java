package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
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
    public Result updateBindMobile(UpdateBindMobileApiParams updateBindMobileApiParams) {
        Result result = new APIResultSupport(false);

        //检查新手机号是否已经绑定了其他用户
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(updateBindMobileApiParams.getNewMobile());
        Result resultQuery = this.queryPassportIdByMobile(baseMoblieApiParams);
        switch(resultQuery.getCode()){
            //新手机已经绑定了其他用户
            case "0":
                String oldUserId = resultQuery.getModels().get("userid").toString();
                if(oldUserId.trim().equals(updateBindMobileApiParams.getUserid().trim())){
                    result.setSuccess(true);
                    result.setMessage("用户目前已经绑定了新手机号");
                }else{
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                    result.setMessage("新手机已经绑定其他用户");
                }
                return result;
            //新手机为绑定其他用户
            case ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND:
                break;
            //其他情况直接返回
            default:
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                result.setMessage("绑定失败");
                return result;
        }

        //检查绑定关系
        if (!this.checkBind(updateBindMobileApiParams.getUserid(), updateBindMobileApiParams.getOldMobile())) {
            result.setMessage("原手机与用户绑定关系检查失败");
        }


        //解除老的绑定的关系
        result= this.unBindMobile(updateBindMobileApiParams.getOldMobile());
        if(!result.isSuccess()){
            return result;
        }


        //建立新的绑定关系
        BindMobileApiParams bindMobileApiParams=new BindMobileApiParams();
        bindMobileApiParams.setMobile(updateBindMobileApiParams.getNewMobile());
        bindMobileApiParams.setUserid(updateBindMobileApiParams.getUserid());
        result = this.bindMobile(bindMobileApiParams);


        //如果绑定新手机失败了，将老手机再绑回去，做一个回滚
        if(!result.isSuccess()){
            BindMobileApiParams bindMobileApiParamsOld=new BindMobileApiParams();
            bindMobileApiParamsOld.setMobile(updateBindMobileApiParams.getOldMobile());
            bindMobileApiParamsOld.setUserid(updateBindMobileApiParams.getUserid());
            this.bindMobile(bindMobileApiParamsOld);
        }
        return result;
    }

    /**
     * 检查用户是否绑定了mobile
     * @param userid
     * @param mobile
     * @return
     */
    private boolean checkBind(String userid, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        Result result = this.queryPassportIdByMobile(baseMoblieApiParams);
        if (result.isSuccess() && result.getModels().containsKey("userid")) {
            String bindUserId = result.getModels().get("userid").toString();
            if (bindUserId.trim().equals(userid.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解除绑定
     *
     * @param mobile 手机号
     * @return
     */
    private Result unBindMobile(String mobile) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
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
        return executeResult(requestModelXml,baseMoblieApiParams.getMobile());
    }

}
