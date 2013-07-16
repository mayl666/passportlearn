package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:20
 */
@Component("proxyUserInfoApiManagerImpl")
public class ProxyUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

    private static Set<String> SUPPORT_FIELDS_MAP=null;

    static{
        SUPPORT_FIELDS_MAP=new HashSet<>(8);
        SUPPORT_FIELDS_MAP.add("birthday");
        SUPPORT_FIELDS_MAP.add("gender");
        SUPPORT_FIELDS_MAP.add("sec_mobile");
        SUPPORT_FIELDS_MAP.add("sec_email");
        SUPPORT_FIELDS_MAP.add("sec_ques");
        SUPPORT_FIELDS_MAP.add("province");
        SUPPORT_FIELDS_MAP.add("city");
        SUPPORT_FIELDS_MAP.add("personalid");
    }

    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_USER_INFO, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        String fields = getUserInfoApiparams.getFields();
        String[] fieldList = fields.split(",");
        for (String field : fieldList) {
            if(SUPPORT_FIELDS_MAP.contains(field)){
                requestModelXml.addParam(field, "");
            }
        }
        requestModelXml.addParams(getUserInfoApiparams);
        if(PhoneUtil.verifyPhoneNumberFormat(getUserInfoApiparams.getUserid())){
            requestModelXml.addParam("usertype",1);
        }
        requestModelXml.deleteParams("fields");
        requestModelXml = this.replaceGetUserInfoParams(requestModelXml);
        Result result= this.executeResult(requestModelXml);
        return getUserInfoResultHandel(result);
    }

    /**
     * SHPP参数名和SGPP参数名不一样，在这里做了相关的转换
     * @param requestModelXml
     * @return
     */
    private RequestModelXml replaceGetUserInfoParams(final RequestModelXml requestModelXml){
        if(requestModelXml.containsKey("sec_email")){
            requestModelXml.addParam("email","");
            requestModelXml.addParam("emailflag","");
            requestModelXml.deleteParams("sec_email");
        }
        if(requestModelXml.containsKey("sec_mobile")){
            requestModelXml.addParam("mobile","");
            requestModelXml.addParam("mobileflag","");
            requestModelXml.deleteParams("sec_mobile");
        }
        if(requestModelXml.containsKey("sec_ques")){
            requestModelXml.addParam("question","");
            requestModelXml.deleteParams("sec_ques");
        }
        return requestModelXml;
    }

    /**
     * SHPP所使用的一些数据名称和SGPP
     * @param result
     * @return
     */
    private Result getUserInfoResultHandel(final Result result){
        if(!result.isSuccess()){
            return result;
        }
        //判断手机和邮箱是否是绑定的同时将SHPP的相关属性转换为SGPP的属性
        Map<String, String> map = result.getModels();

        if(map.containsKey("email")){
            String email = map.get("email");
            String emailflag = map.get("emailflag");
            if (StringUtil.isBlank(emailflag) || !emailflag.trim().equals("1")) {
                email = "";
            }
            map.put("sec_email", email);
            map.remove("email");
            map.remove("emailflag");
        }

        if(map.containsKey("mobile")){
            String mobile = map.get("mobile");
            String mobileflag = map.get("mobileflag");
            if (StringUtil.isBlank(mobileflag) || !mobileflag.trim().equals("1")) {
                mobile = "";
            }
            map.put("sec_mobile", mobile);
            map.remove("mobile");
            map.remove("mobileflag");
        }

        if(map.containsKey("question")){
            String question = map.get("question");
            map.put("sec_ques", question);
            map.remove("question");
        }
        result.setModels(map);
        return result;
    }


    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        if(PhoneUtil.verifyPhoneNumberFormat(updateUserInfoApiParams.getUserid())){
            String userid=updateUserInfoApiParams.getUserid();
            userid+="@sohu.com";
            updateUserInfoApiParams.setUserid(userid);
        }
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
