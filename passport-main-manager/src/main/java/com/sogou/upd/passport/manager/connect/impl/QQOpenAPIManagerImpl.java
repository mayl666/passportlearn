package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mysql.jdbc.StringUtils;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.apache_asynhttpclient.ApacheAsynHttpClient;
import com.sogou.upd.passport.common.asynchttpclient.AsyncHttpClientService;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.connect.QQOpenAPIManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpenApiV3;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuthError;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-14
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class QQOpenAPIManagerImpl implements QQOpenAPIManager {

    private Logger logger = LoggerFactory.getLogger("friendsLogger");

    private String QQ_RET_CODE = "0";
    private String QQ_RET_CODE_FOR_MODIFY_PASSPORT = "-73";
    private String QQ_RET_CODE_FOR_TOKEN_EXPIRE = "100014";
    private String QQ_RET_CODE_FOR_TOKEN_INVALID = "100015";
    private String QQ_RET_CODE_FOR_NO_AUTHORITY = "100030";

    private static final String GET_QQ_FRIENDS_AES_URL = "http://203.195.155.61:8888/internal/qq/friends_aesinfo";
//    private static final String GET_QQ_FRIENDS_AES_URL = "http://qqfriends.gz.1251021740.clb.myqcloud.com/internal/qq/friends_aesinfo";

    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";
    private static final String CACHE_PREFIX_QQFRIEND = CacheConstant.CACHE_KEY_QQ_FRIENDS;

    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    public static AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();

    public String buildQQFriendsCacheKey(String userid, String third_appid) {
        return CACHE_PREFIX_QQFRIEND + userid + "_" + third_appid;
    }


    public Result getQQFriends(String userid, String tkey, String third_appid) throws Exception {
        String cacheKey = buildQQFriendsCacheKey(userid, third_appid);
        List cachelist = dbShardRedisUtils.getObject(cacheKey, List.class);
        Result result = new APIResultSupport(false);
        if ((null != cachelist) &&(!cachelist.isEmpty()) ) {
            result.setSuccess(true);
            result.setDefaultModel("items", cachelist);
            return result;
        } else {
            result = new APIResultSupport(false);
            RequestModel requestModel = new RequestModel(GET_QQ_FRIENDS_AES_URL);
            requestModel.addParam("userid", userid);
            requestModel.addParam("tKey", tkey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
//            String returnVal = SGHttpClient.executeStr(requestModel);
            String returnVal = ApacheAsynHttpClient.executeStr(requestModel);
//            asyncHttpClientService.sendPreparePost(GET_QQ_FRIENDS_AES_URL);
            String str = AES.decryptURLSafeString(returnVal, TKEY_SECURE_KEY);
            Map map = JacksonJsonMapperUtil.getMapper().readValue(str, Map.class);
            if (!CollectionUtils.isEmpty(map)) {
                if (map.containsKey("ret")) {
                    String ret = String.valueOf(map.get("ret"));
                    if (QQ_RET_CODE.equals(ret)) {
                        result.setSuccess(true);
                        if (map.containsKey("items")) {
                            List<Map<String, Object>> list = changePassportId((List<Map<String, Object>>) map.get("items"), third_appid, userid);
                            result.setDefaultModel("items", list);
                            dbShardRedisUtils.setObjectWithinSeconds(cacheKey, list, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                        }
                    } else if (QQ_RET_CODE_FOR_MODIFY_PASSPORT.equals(ret)) {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_TOKEN_PWDERROR);
                    } else if (QQ_RET_CODE_FOR_TOKEN_EXPIRE.equals(ret) || QQ_RET_CODE_FOR_TOKEN_INVALID.equals(ret)) {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
                    } else if (QQ_RET_CODE_FOR_NO_AUTHORITY.equals(ret)) {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY);
                    } else {

                        logger.error("return value error ：" + map.toString());
                        if (map.containsKey("msg")) {
                            result.setMessage(String.valueOf(map.get("msg")));
                        }
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                    }
                } else {
                    logger.error("return value error ：" + map.toString());
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                }
            } else {
                logger.error("return value error,no value！");
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
            }
            return result;
        }

    }

    @Override
    public ConnectUserInfoVO getQQUserInfo(String openId, String openKey, ConnectConfig connectConfig) throws OAuthProblemException {
        ConnectUserInfoVO connectUserInfoVO;
        Map resultMap;
        //QQ提供的openapi服务器
        String serverName = CommonConstant.QQ_SERVER_NAME_GRAPH;
        //应用的基本信息，搜狗在QQ的第三方appid与appkey
        OpenApiV3 sdk = new OpenApiV3(connectConfig.getAppKey(), connectConfig.getAppSecret());
        sdk.setServerName(serverName);
        //调用代理第三方接口，点亮或熄灭QQ图标
        //https://graph.qq.com/v3/user/get_info?openid=FF46B08FC3D97E66CCDB61FA14C78805&openkey=C569E9F7CC67311C800E6A6A89EBC9DE&pf=qzone&appid=100294784&format=json
        // 指定OpenApi Cgi名字
        String scriptName = "/v3/user/get_info";
        // 指定HTTP请求协议类型,目前代理接口走的都是HTTP请求，所以需要sig签名，如果为HTTPS请求，则不需要sig签名
        String protocol = CommonConstant.HTTPS;
        // 填充URL请求参数,用来生成sig签名
        HashMap<String, String> params = Maps.newHashMap();
        params.put("openid", openId);
        params.put("openkey", openKey);
        params.put("pf", "qzone");
        try {
            resultMap = sdk.api(scriptName, params, protocol);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(resultMap)) {
            throw OAuthProblemException.error(ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        }
        int ret = (Integer) resultMap.get(QQOAuthError.ERROR_CODE);
        if (ret != 0) {
            throw OAuthProblemException.error(ErrorUtil.CONNECT_USER_DEFINED_ERROR, (String) resultMap.get(QQOAuthError.ERROR_DESCRIPTION));
        }
        connectUserInfoVO = new ConnectUserInfoVO();
        connectUserInfoVO.setNickname(getParam(resultMap, QQOAuth.NICK_NAME));
        connectUserInfoVO.setAvatarSmall(formAvatarUrl(resultMap, "30"));    // 30*30
        connectUserInfoVO.setAvatarMiddle(formAvatarUrl(resultMap, "50"));  // 50*50
        connectUserInfoVO.setAvatarLarge(formAvatarUrl(resultMap, "100"));   // 100*100
        connectUserInfoVO.setGender(formGender(getParam(resultMap, QQOAuth.GENDER)));
        connectUserInfoVO.setOriginal(resultMap);

        return connectUserInfoVO;
    }

    public List<Map<String, Object>> changePassportId(List<Map<String, Object>> list, String third_appid, String userid) {
        if (!CollectionUtils.isEmpty(list)) {
            List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                String openid = String.valueOf(map.get("openid"));
                if (!StringUtils.isNullOrEmpty(openid)) {
                    Result result = sgConnectApiManager.getConnectRelation(openid, AccountTypeEnum.QQ.getValue(), third_appid);
                    if (!result.isSuccess()) {
                        logger.error("connectRelation has no this openid,remove,userid : " + userid + "third_appid : " + third_appid + "，openid : " + openid);
                        removeList.add(map);
                        continue;
                    } else {
                        ConnectRelation connectRelation = (ConnectRelation) result.getModels().get("connectRelation");
                        map.put("userid", connectRelation.getPassportId());
                        map.remove("openid");
                    }
                }
            }
            logger.warn("tencent return size：" + list.size() + "removed size ：" + removeList.size());
            list.removeAll(removeList);
        }
        return list;
    }

    //------------处理QQ UserInfo OpenAPI返回结果-----------------
    private String getParam(Map resultMap, String param) {
        Object value = resultMap.get(param);
        return value == null ? null : String.valueOf(value);
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("男")) {
            sex = 1;
        }
        return sex;
    }

    private String formAvatarUrl(Map resultMap, String size) {
        Object value = resultMap.get("figureurl");
        if (value == null || Strings.isNullOrEmpty(String.valueOf(value))) {
            return null;
        } else {
            String url = String.valueOf(value);
            return url.substring(0, url.lastIndexOf("/")) + "/" + size;
        }
    }

}
