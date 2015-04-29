package com.sogou.upd.passport.web.inteceptor;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.MongodbConstant;
import com.sogou.upd.passport.common.mongodb.util.MongoServerUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.common.utils.JodaTimeUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.annotation.RiskControlSecurity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-24
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class RiskControlInterceptor extends HandlerInterceptorAdapter {

    private static final Logger fileLog = LoggerFactory.getLogger("com.sogou.upd.passport.riskControlFileAppender");
    private static final Logger log = LoggerFactory.getLogger(RiskControlInterceptor.class);

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String LOG_JOINER_STR = "\t";

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
                String key = buildDenyIpKey(ip);
//                String redisVal = redisUtils.get(key);
//                if (Strings.isNullOrEmpty(redisVal)) {

                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.put(MongodbConstant.IP, ip);

                DBObject resultObject = mongoServerUtil.findOne(MongodbConstant.RISK_CONTROL_COLLECTION, basicDBObject);
                if (null != resultObject) {
                    //0:国内、1:国外
                    String regional = String.valueOf(resultObject.get(MongodbConstant.REGIONAL));
                    String endTimeStr = String.valueOf(resultObject.get(MongodbConstant.DENY_END_TIME));
                    if (!Strings.isNullOrEmpty(endTimeStr) && !Strings.isNullOrEmpty(regional)) {
                        //非线程安全
//                        Date endTime = dateFormatter.parse(endTimeStr);
//                        Date nowTime = new Date();
//                        if (endTime.after(nowTime)) {
//                            log.warn("封禁记录: " + resultObject.toString());
//                            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
//                            redisUtils.set(key, resultObject.toString(), (endTime.getTime() - nowTime.getTime()), TimeUnit.MILLISECONDS);
//                        } else {
//                            return true;
//                        }

                        //共用出口IP 标记
                        boolean isSharedIp = false;
                        //国内、国外IP 标记
                        boolean isForeignIp = true;
                        if (MongodbConstant.CHINA_IP.equalsIgnoreCase(regional)) {
                            isForeignIp = false;
                            DBObject dbObject = mongoServerUtil.findOne(MongodbConstant.IP_SHARED_EXPORT_DATABASE, basicDBObject);
                            if (null != dbObject) {
                                isSharedIp = true;
                            }
                        }
                        //国外IP、国内非共用出口IP,实施封禁
                        if (isForeignIp || !isSharedIp) {
                            //DateTimeFormat 是线程安全而且不变
                            DateTime denyEndTime = JodaTimeUtil.parseToDateTime(endTimeStr, JodaTimeUtil.SECOND);
                            DateTime nowDateTime = new DateTime();
                            if (denyEndTime.isAfter(nowDateTime)) {
                                fileLog.warn(ip + LOG_JOINER_STR + resultObject.get(MongodbConstant.REGIONAL) + LOG_JOINER_STR + resultObject.get(MongodbConstant.DENY_START_TIME + LOG_JOINER_STR + resultObject.get(MongodbConstant.DENY_END_TIME) + LOG_JOINER_STR + resultObject.get(MongodbConstant.RATE)));
                                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                                redisUtils.set(key, resultObject.toString(), (denyEndTime.toDate().getTime() - nowDateTime.toDate().getTime()), TimeUnit.MILLISECONDS);
                            } else {
                                return true;
                            }
                        }

                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
//                } else {
//                    log.warn("封禁记录 ： " + redisVal.toString());
//                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
//                }

            } catch (Exception e) {
                log.error("RiskControlInterceptor Exception : " + e);
                return true;
            }
        }
        ResponseResultType resultType = security.resultType();
        String msg;
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

    /**
     * 生成存储封禁IP的 redis key
     *
     * @param ip
     * @return
     */
    private static String buildDenyIpKey(String ip) {
        return CacheConstant.CACHE_PREFIX_DENY_IP + ip;
    }
}
