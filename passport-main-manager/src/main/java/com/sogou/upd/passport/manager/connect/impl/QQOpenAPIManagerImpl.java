package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.connect.QQOpenAPIManager;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    private Logger logger = LoggerFactory.getLogger(QQOpenAPIManagerImpl.class);

    private String QQ_RET_CODE = "0";

    private static final String GET_QQ_FRIENDS_AES_URL = "http://203.195.155.61:80/internal/qq/friends_aesinfo";

    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";

    private static final String CACHE_PREFIX_QQFRIEND =
            CacheConstant.CACHE_KEY_QQ_FRIENDS;

    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    public String buildQQFriendsCacheKey(String userid, String third_appid) {
        return CACHE_PREFIX_QQFRIEND + userid + "_" + third_appid;
    }


    public String get_qqfriends(String userid, String tkey, String third_appid) throws Exception {
        String cacheKey = buildQQFriendsCacheKey(userid, third_appid);
        String resultVal = dbShardRedisUtils.get(cacheKey);
        if (!Strings.isNullOrEmpty(resultVal)) {
            return resultVal;
        } else {
            Result result = new APIResultSupport(false);
            RequestModel requestModel = new RequestModel(GET_QQ_FRIENDS_AES_URL);
            requestModel.addParam("userid", userid);
            requestModel.addParam("tKey", tkey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            long start = System.currentTimeMillis();
            String returnVal = SGHttpClient.executeStr(requestModel);
            String str = AES.decryptURLSafeString(returnVal, TKEY_SECURE_KEY);
            Map map = JacksonJsonMapperUtil.getMapper().readValue(str, Map.class);
//            String str = SGHttpClient.executeForBigData(requestModel);

//            Map inParammap = new HashMap();
//            inParammap.put("userid", userId);
//            inParammap.put("tKey", tKey);
//            String str = this.send(QQ_FRIENDS_URL,"POST",inParammap,null);
//            Pair<Integer, String> pair = HttpClientUtil.post(QQ_FRIENDS_URL, inParammap);
//            Map map = JacksonJsonMapperUtil.getMapper().readValue(pair.getRight(), Map.class);

            logger.warn("SGHttpClient.executeStr time : " + (System.currentTimeMillis() - start));

            if (!CollectionUtils.isEmpty(map)) {
                if (map.containsKey("ret")) {
                    String ret = String.valueOf(map.get("ret"));
                    if (QQ_RET_CODE.equals(ret)) {
                        result.setSuccess(true);
                        if (map.containsKey("items")) {
                            List<Map<String, Object>> list = changePassportId((List<Map<String, Object>>) map.get("items"), third_appid);
                            result.setDefaultModel("items", list);
                            dbShardRedisUtils.setStringWithinSeconds(cacheKey, result.toString(), DateAndNumTimesConstant.TIME_ONEDAY);
                        }
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
//                map = changeResult(map, third_appid);
                //调用返回
//                resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
            } else {
                logger.error("return value error,no value！");
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
            }
            return result.toString();
        }

    }

    public List<Map<String, Object>> changePassportId(List<Map<String, Object>> list, String third_appid) {
        if (!CollectionUtils.isEmpty(list)) {
            List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                String openid = String.valueOf(map.get("openid"));
                if (!StringUtils.isNullOrEmpty(openid)) {
                    Result result = sgConnectApiManager.getConnectRelation(openid, AccountTypeEnum.QQ.getValue(), third_appid);
                    if (!result.isSuccess()) {
                        logger.error("connectRelation has no this openid,remove，openid : " + openid);
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
}
