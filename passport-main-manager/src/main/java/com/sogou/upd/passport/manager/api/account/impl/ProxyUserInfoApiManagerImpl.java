package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: ligang201716@sogou-inc.com
 * edit: mayan
 * Date: 13-6-13
 * Time: 上午11:20
 */
@Component("proxyUserInfoApiManager")
public class ProxyUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

    private static Set<String> SUPPORT_FIELDS_MAP = null;

    static {
        SUPPORT_FIELDS_MAP = new HashSet<>(8);
        SUPPORT_FIELDS_MAP.add("birthday");//生日
        SUPPORT_FIELDS_MAP.add("gender");//性别
        SUPPORT_FIELDS_MAP.add("sec_mobile");//密保手机
        SUPPORT_FIELDS_MAP.add("sec_email");//密保邮箱
        SUPPORT_FIELDS_MAP.add("sec_ques");//密保问题
        SUPPORT_FIELDS_MAP.add("province");//身份
        SUPPORT_FIELDS_MAP.add("city");//城市
        SUPPORT_FIELDS_MAP.add("personalid");//身份证号
        SUPPORT_FIELDS_MAP.add("username"); //用户真实姓名
        SUPPORT_FIELDS_MAP.add("uniqname"); //用户昵称
        SUPPORT_FIELDS_MAP.add("avatarurl"); //用户头像
        SUPPORT_FIELDS_MAP.add("createtime");//创建时间
        SUPPORT_FIELDS_MAP.add("createip");//创建ip
        SUPPORT_FIELDS_MAP.add("mobile");//todo 兼容搜狗流程获取安全信息的参数，密保手机
        SUPPORT_FIELDS_MAP.add("email");//todo 兼容搜狗流程获取安全信息的参数，密保邮箱
        SUPPORT_FIELDS_MAP.add("question");//todo 兼容搜狗流程获取安全信息的参数，密保问题
    }

    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        Result result = null;
        try {
            //搜狐真实姓名 username,搜狗fullname
            String fields = getUserInfoApiparams.getFields();
            if (!Strings.isNullOrEmpty(fields) && fields.contains("fullname")) {
                fields = fields.replace("fullname", "username");
                getUserInfoApiparams.setFields(fields);
            }

            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_USER_INFO, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            String[] fieldList = fields.split(",");
            for (String field : fieldList) {
                if (SUPPORT_FIELDS_MAP.contains(field)) {
                    requestModelXml.addParam(field, "");
                }
            }
            requestModelXml.addParams(getUserInfoApiparams);
            requestModelXml.deleteParams("imagesize");

            if (PhoneUtil.verifyPhoneNumberFormat(getUserInfoApiparams.getUserid())) {
                requestModelXml.addParam("usertype", 1);
            }
            requestModelXml.deleteParams("fields");
            requestModelXml = this.replaceGetUserInfoParams(requestModelXml);
            result = getUserInfoResultHandel(this.executeResult(requestModelXml));

            if (result.isSuccess()) {
                //获取完把搜狐真实姓名username替换成 搜狗的fullname
                String fullname = (String) result.getModels().get("username");
                if (!Strings.isNullOrEmpty(fullname)) {
                    //搜狐真实姓名变为utf-8编码
                    result.setDefaultModel("fullname", fullname);
                    result.getModels().remove("username");
                }

                //替换搜狐的个人头像
                String avatarurl = result.getModels().get("avatarurl") != null ? (String) result.getModels().get("avatarurl") : null;
                result.setDefaultModel("avatarurl", avatarurl);
            }
        } catch (Exception e) {
        }
        return getUserInfoResultHandel(result);
    }

    /**
     * SHPP参数名和SGPP参数名不一样，在这里做了相关的转换
     *
     * @param requestModelXml
     * @return
     */
    private RequestModelXml replaceGetUserInfoParams(final RequestModelXml requestModelXml) {
        //todo email、mobile、question兼容搜狗流程获取安全信息的参数
        if (requestModelXml.containsKey("sec_email") || requestModelXml.containsKey("email")) {
            requestModelXml.addParam("email", "");
            requestModelXml.addParam("emailflag", "");
            requestModelXml.deleteParams("sec_email");
        }
        if (requestModelXml.containsKey("sec_mobile") || requestModelXml.containsKey("mobile")) {
            requestModelXml.addParam("mobile", "");
            requestModelXml.addParam("mobileflag", "");
            requestModelXml.deleteParams("sec_mobile");
        }
        if (requestModelXml.containsKey("sec_ques") || requestModelXml.containsKey("question")) {
            requestModelXml.addParam("question", "");
            requestModelXml.deleteParams("sec_ques");
        }
        return requestModelXml;
    }

    /**
     * SHPP所使用的一些数据名称和SGPP
     *
     * @param result
     * @return
     */
    private Result getUserInfoResultHandel(final Result result) {
        if (!result.isSuccess()) {
            return result;
        }
        //判断手机和邮箱是否是绑定的同时将SHPP的相关属性转换为SGPP的属性
        Map<String, String> map = result.getModels();

        if (map.containsKey("email")) {
            String email = map.get("email");
            String emailflag = map.get("emailflag");
            if (StringUtil.isBlank(emailflag) || !emailflag.trim().equals("1")) {
                email = "";
            }
            map.put("sec_email", email);
            map.remove("email");
            map.remove("emailflag");
        }

        if (map.containsKey("mobile")) {
            String mobile = map.get("mobile");
            String mobileflag = map.get("mobileflag");
            if (StringUtil.isBlank(mobileflag) || !mobileflag.trim().equals("1")) {
                mobile = "";
            }
            map.put("sec_mobile", mobile);
            map.remove("mobile");
            map.remove("mobileflag");
        }

        if (map.containsKey("question")) {
            String question = map.get("question");
            map.put("sec_ques", question);
            map.remove("question");
        }
        result.setModels(map);
        return result;
    }


    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        return null;
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {
//        if (updateUserUniqnameApiParams.getUniqname() == null || "".equals(updateUserUniqnameApiParams.getUniqname())) {
//            throw new IllegalArgumentException("用户昵称不能为空");
//        }
//        updateUserUniqnameApiParams.setUniqname(updateUserUniqnameApiParams.getUniqname());
//        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_USER_UNIQNAME, "info");
//        requestModelXml.addParams(updateUserUniqnameApiParams);
//        Result result = executeResult(requestModelXml, updateUserUniqnameApiParams.getUniqname());
//        if (result.isSuccess()) {
//            result.setMessage("昵称未被占用");
//        }
//        return result;
        return null;
    }
}
