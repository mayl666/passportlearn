package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXmlGBK;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:20
 */
@Component("proxyUserInfoApiManager")
public class ProxyUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

    private static Set<String> SUPPORT_FIELDS_MAP = null;

    @Autowired
    private RedisUtils redisUtils;
    @Inject
    private PhotoUtils photoUtils;

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
    }

    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        Result result=null;
        try {
            //搜狐真实姓名 username,搜狗fullname
            String fields=getUserInfoApiparams.getFields();
            if(!Strings.isNullOrEmpty(fields) && fields.contains("fullname")) {
                fields=fields.replace("fullname","username");
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
        if (PhoneUtil.verifyPhoneNumberFormat(getUserInfoApiparams.getUserid())) {
            requestModelXml.addParam("usertype", 1);
        }
        requestModelXml.deleteParams("fields");
        requestModelXml = this.replaceGetUserInfoParams(requestModelXml);
            result = getUserInfoResultHandel(this.executeResult(requestModelXml));

            if(result.isSuccess()){
                //获取完把搜狐真实姓名username替换成 搜狗的fullname
                String fullname= (String) result.getModels().get("username");
                if(!Strings.isNullOrEmpty(fullname)){
                    //搜狐真实姓名变为utf-8编码
                    result.setDefaultModel("fullname",fullname);
                    result.getModels().remove("username");
                }

                //替换搜狐的个人头像
                String avatarurl= result.getModels().get("avatarurl")!=null ?(String)result.getModels().get("avatarurl"):null;
                String image= Strings.isNullOrEmpty(avatarurl)?null:avatarurl.replaceAll("\\/\\/","");
                String passportId= getUserInfoApiparams.getUserid();

                if(!Strings.isNullOrEmpty(image)){
                    String cacheKey="SP.PASSPORTID:IMAGE_"+passportId;

                    Map<String,String> map=redisUtils.hGetAll(cacheKey) ;
                    if(MapUtils.isNotEmpty(map)){
                        String shImg=map.get("shImg");
                        String sgImg=map.get("sgImg");
                        if(!shImg.equals(image)){
                            //获取图片名
                            String imgName = photoUtils.generalFileName();
                            // 上传到OP图片平台
                            if (photoUtils.uploadImg(imgName, null,image,"1")) {
                                sgImg = photoUtils.accessURLTemplate(imgName);
                                Map<String,String> mapResult= Maps.newHashMap();
                                mapResult.put("shImg",image);
                                mapResult.put("sgImg",sgImg);

                                redisUtils.hPutAll(cacheKey,mapResult);
                            } else {
                                result.setCode(ErrorUtil.ERR_UPLOAD_PHOTO);
                                return result;
                            }
                        }
                        result.setDefaultModel("image",obtainPhoto(sgImg,"50"));
                        result.getModels().remove("avatarurl");
                    } else {
                        //获取图片名
                        String imgName = photoUtils.generalFileName();
                        // 上传到OP图片平台
                        if (photoUtils.uploadImg(imgName, null,image,"1")) {
                            String sgImg = photoUtils.accessURLTemplate(imgName);
                            Map<String,String> mapResult= Maps.newHashMap();
                            mapResult.put("shImg",image);
                            mapResult.put("sgImg",sgImg);

                            redisUtils.hPutAll(cacheKey,mapResult);
                    }
                }
            }
            }
        }catch (Exception e){
             e.printStackTrace();
        }
        return result;
    }

    public String obtainPhoto(String imgURL,String size){
            String clientId=photoUtils.getAppIdBySize(size);

            //随机获取cdn域名
            String cdnUrl=photoUtils.getCdnURL();
            return  String.format(imgURL,cdnUrl, clientId);
    }

    /**
     * SHPP参数名和SGPP参数名不一样，在这里做了相关的转换
     *
     * @param requestModelXml
     * @return
     */
    private RequestModelXml replaceGetUserInfoParams(final RequestModelXml requestModelXml) {
        if (requestModelXml.containsKey("sec_email")) {
            requestModelXml.addParam("email", "");
            requestModelXml.addParam("emailflag", "");
            requestModelXml.deleteParams("sec_email");
        }
        if (requestModelXml.containsKey("sec_mobile")) {
            requestModelXml.addParam("mobile", "");
            requestModelXml.addParam("mobileflag", "");
            requestModelXml.deleteParams("sec_mobile");
        }
        if (requestModelXml.containsKey("sec_ques")) {
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
        if (PhoneUtil.verifyPhoneNumberFormat(updateUserInfoApiParams.getUserid())) {
            String userid = updateUserInfoApiParams.getUserid();
            userid += "@sohu.com";
            updateUserInfoApiParams.setUserid(userid);
        }
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_USER_INFO, "info");
        Map<String, Object> fields = BeanUtil.beanDescribe(updateUserInfoApiParams);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtil.isBlank(key) || value == null || StringUtil.isBlank(value.toString())) {
                continue;
            }
            requestModelXml.addParam(key, value);
        }
        String birthday = updateUserInfoApiParams.getBirthday();
        if (!Strings.isNullOrEmpty(birthday)) {
//            String birthdayStr = DateUtil.formatDate(birthday);
            requestModelXml.addParam("birthday", birthday);
        }


        return this.executeResult(requestModelXml);
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {
        if (updateUserUniqnameApiParams.getUniqname() == null || "".equals(updateUserUniqnameApiParams.getUniqname())) {
            throw new IllegalArgumentException("用户昵称不能为空");
        }

        try {
//            updateUserUniqnameApiParams.setUniqname(URLDecoder.decode(updateUserUniqnameApiParams.getUniqname(), "utf-8"));
            updateUserUniqnameApiParams.setUniqname(updateUserUniqnameApiParams.getUniqname());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UPDATE_USER_UNIQNAME, "info");
        requestModelXml.addParams(updateUserUniqnameApiParams);
        Result result = executeResult(requestModelXml, updateUserUniqnameApiParams.getUniqname());
        if(result.isSuccess()){
            result.setMessage("昵称未被占用");
        }
        return result;
    }
}
