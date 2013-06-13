package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:20
 */
@Component("proxyUserInfoApiManagerImpl")
public class ProxyUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {


    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_USER_INFO, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        String fields = getUserInfoApiparams.getFields();
        String[] fieldList = fields.split(",");
        for (String field : fieldList) {
            requestModelXml.addParam(field, "");
        }
        requestModelXml.addParams(getUserInfoApiparams);
        requestModelXml.deleteParams("fields");
        return this.executeResult(requestModelXml);
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_USER_INFO, "register");
        Map<String, Object> fields = BeanUtil.beanDescribe(updateUserInfoApiParams);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtil.isBlank(key) || value == null || StringUtil.isBlank(value.toString())) {
                continue;
            }
            requestModelXml.addParam(key, value);
        }
        Date birthday=updateUserInfoApiParams.getBirthday();
        if(birthday!=null){
            String birthdayStr=DateUtil.formatDate(birthday);
            requestModelXml.addParam("birthday", birthdayStr);
        }
        return this.executeResult(requestModelXml);
    }
}
