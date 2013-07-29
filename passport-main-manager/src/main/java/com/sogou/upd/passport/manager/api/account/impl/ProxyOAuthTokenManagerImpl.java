package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.OAuthTokenManager;
import com.sogou.upd.passport.manager.form.RefreshPcTokenParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyOAuthTokenManager")
public class ProxyOAuthTokenManagerImpl extends BaseProxyManager implements OAuthTokenManager {
    private static Logger log = LoggerFactory.getLogger(ProxyOAuthTokenManagerImpl.class);

    public Result refreshToken(RefreshPcTokenParams refreshPcTokenParams){
        Result result = new APIResultSupport(false);
        RequestModel requestModel = new RequestModel(SHPPUrlConstant.SOHU_REFRESHTOKEN_URL);
        requestModel.addParams(refreshPcTokenParams);
        String resultStr = SGHttpClient.executeStr(requestModel);
        if(!StringUtils.isEmpty(resultStr)){
            String[] resultArr = resultStr.split("\\|");
            if(resultArr.length > 0){
               if("0".equals(resultArr[0])){
                   result.setSuccess(true);
               }else {
                   result.setCode(resultArr[0]);
               }
            }
        }
        return result;
    }
}
