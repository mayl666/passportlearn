package com.sogou.upd.passport.web.inteceptor;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.MongodbConstant;
import com.sogou.upd.passport.common.mongodb.util.MongoServerUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.annotation.RiskControlSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-24
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class RiskControlInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(RiskControlInterceptor.class);

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public MongoServerUtil mongoServerUtil;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RiskControlSecurity security = handlerMethod.getMethodAnnotation(RiskControlSecurity.class);
        if (security == null) {
            return true;
        }
        Result result = new APIResultSupport(false);
        String ip = IpLocationUtil.getIp(request);
        if (Strings.isNullOrEmpty(ip)) {
            return true;
        } else {
            try {
                String key = buildMongoDBBlackListKey(ip);
                String redisVal = redisUtils.get(key);
                if (Strings.isNullOrEmpty(redisVal)) {
                    DBCollection dbCollection = mongoServerUtil.getCollection(MongodbConstant.RISK_CONTROL_COLLECTION_TEST);
                    BasicDBObject basicDBObject = new BasicDBObject();
                    basicDBObject.put("ip", ip);
                    DBObject resultObject = dbCollection.findOne(basicDBObject);
                    if (null != resultObject) {
                        String endTimeStr = String.valueOf(resultObject.get("deny_endTime"));
                        if (!Strings.isNullOrEmpty(endTimeStr)) {
                            Date endTime = dateFormatter.parse(endTimeStr);
                            Date nowTime = new Date();
                            if (endTime.after(nowTime)) {
                                log.info("封禁记录 ： " + resultObject.toString());
                                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                                redisUtils.set(key, resultObject.toString(), (endTime.getTime() - nowTime.getTime()), TimeUnit.MILLISECONDS);
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    log.info("封禁记录 ： " + redisVal.toString());
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                }

            } catch (Exception e) {
                log.error("RiskControlInterceptor Exception : " + e);
                return true;
            }
        }
        ResponseResultType resultType = security.resultType();
        String msg = "";
        switch (resultType) {
            case json:
                msg = result.toString();
                response.setContentType(HttpConstant.ContentType.JSON + ";charset=UTF-8");
                response.getWriter().write(msg);
                break;
            case xml:
            case txt:
            case forward:
            case redirect:
        }
        return false;
    }


    public String buildMongoDBBlackListKey(String ip) {
        return "SP.BLACKLIST_IP:IP_" + ip;
    }
}
