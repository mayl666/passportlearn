package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.StringUtil;
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

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:26
 */
@Component("proxySecureApiManager")
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

    @Override
    public Result getUserSecureInfo(GetSecureInfoApiParams getSecureInfoApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.USER_INFO, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParam("userid", getSecureInfoApiParams.getUserid());
        requestModelXml.addParam("email","");
        requestModelXml.addParam("emailflag","");
        requestModelXml.addParam("mobile","");
        requestModelXml.addParam("mobileflag","");
        requestModelXml.addParam("question","");

        Result result=this.executeResult(requestModelXml);
        if(!result.isSuccess()){
           return result;
        }

        //判断手机和邮箱是否是绑定的同时将SHPP的相关属性转换为SGPP的属性
        Map<String,String> map= result.getModels();
        String email=map.get("email");
        String emailflag=map.get("emailflag");
        String mobile=map.get("mobile");
        String mobileflag=map.get("mobileflag");
        String question=map.get("question");

        Map resultMap= Maps.newHashMapWithExpectedSize(3);
        if(StringUtil.isBlank(emailflag)||!emailflag.trim().equals("1")){
            email=null;
        }

        if(StringUtil.isBlank(mobileflag)||!mobileflag.trim().equals("1")){
            mobile=null;
        }
        resultMap.put("sec_email",email);
        resultMap.put("sec_mobile",mobile);
        resultMap.put("question",question);
        result.setModels(resultMap);
        return result;
    }
}
