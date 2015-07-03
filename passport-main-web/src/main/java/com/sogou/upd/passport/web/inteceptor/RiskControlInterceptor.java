package com.sogou.upd.passport.web.inteceptor;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.MongodbConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.mongodb.util.MongoServerUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.common.utils.JodaTimeUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.annotation.RiskControlSecurity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;
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

    private static final String LOG_JOINER_STR = "\t";
    private static final String CACHE_VALUE_JOINER = "|";
    private static final String DENY_CACHE_HIT = "1";
    private static final String DENY_SET_CACHE = "0";

    @Autowired
    public MongoServerUtil mongoServerUtil;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RiskControlSecurity security = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            security = handlerMethod.getMethodAnnotation(RiskControlSecurity.class);
            //检查是否加@InterfaceSecurity注解，如果没加不需要验证
            if (security == null) {
                return true;
            }
        }
        Result result = new APIResultSupport(false);
        String ip = IpLocationUtil.getIp(request);

        String client_id = ServletRequestUtils.getStringParameter(request, CommonConstant.CLIENT_ID, StringUtils.EMPTY);
        String username = ServletRequestUtils.getStringParameter(request, CommonConstant.USERNAME, StringUtils.EMPTY);
        if (Strings.isNullOrEmpty(ip)) {
            return true;
        } else {
            try {
                //增加白名单处理 ，如果ip在白名单中，直接返回true
                String whiteListKey = CacheConstant.CACHE_PREFIX_LOGIN_WHITELIST;
                Set<String> whiteList = redisUtils.smember(whiteListKey);
                if (CollectionUtils.isNotEmpty(whiteList)) {
                    if (whiteList.contains(ip)) {
                        return true;
                    }
                }

                String key = buildDenyIpKey(ip);
                String cacheValue = redisUtils.get(key);
                if (Strings.isNullOrEmpty(cacheValue)) {
                    BasicDBObject basicDBObject = new BasicDBObject();
                    basicDBObject.put(MongodbConstant.IP, ip);
                    DBObject resultObject = mongoServerUtil.findOne(MongodbConstant.RISK_CONTROL_COLLECTION, basicDBObject);
                    if (null != resultObject) {
                        String regional = String.valueOf(resultObject.get(MongodbConstant.REGIONAL));
                        String endTimeStr = String.valueOf(resultObject.get(MongodbConstant.DENY_END_TIME));
                        if (!Strings.isNullOrEmpty(endTimeStr) && !Strings.isNullOrEmpty(regional)) {
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
                                    String message = buildDenyLogMessage(nowDateTime.toDate(), resultObject);
                                    fileLog.warn(message);
                                    result.setCode(ErrorUtil.ERROR_CODE_RISK_CONTROL_DENY_IP);

                                    String setValue = StringUtils.replace(message, LOG_JOINER_STR, CACHE_VALUE_JOINER);
                                    long cacheTime = denyEndTime.toDate().getTime() - nowDateTime.toDate().getTime();
                                    redisUtils.set(key, setValue, cacheTime, TimeUnit.MILLISECONDS);

                                    //记录操作日志
                                    UserOperationLog userOperationLog = new UserOperationLog(username, request.getRequestURI(), client_id, result.getCode(), ip);
                                    userOperationLog.putOtherMessage(CommonConstant.REFER, request.getHeader(CommonConstant.REFER));
                                    userOperationLog.putOtherMessage(CommonConstant.USER_AGENT, Strings.isNullOrEmpty(request.getHeader(CommonConstant.USER_AGENT)) ? StringUtils.EMPTY : request.getHeader(CommonConstant.USER_AGENT));
                                    UserOperationLogUtil.log(userOperationLog);
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
                } else {
                    String message = buildDenyLogMsg(cacheValue);
                    fileLog.warn(message);
                    result.setCode(ErrorUtil.ERROR_CODE_RISK_CONTROL_DENY_IP);

                    //记录操作日志
                    UserOperationLog userOperationLog = new UserOperationLog(username, request.getRequestURI(), client_id, result.getCode(), ip);
                    userOperationLog.putOtherMessage(CommonConstant.REFER, request.getHeader(CommonConstant.REFER));
                    userOperationLog.putOtherMessage(CommonConstant.USER_AGENT, Strings.isNullOrEmpty(request.getHeader(CommonConstant.USER_AGENT)) ? StringUtils.EMPTY : request.getHeader(CommonConstant.USER_AGENT));
                    UserOperationLogUtil.log(userOperationLog);
                }
            } catch (Exception e) {
                log.error("RiskControlInterceptor Exception : " + e);
                return true;
            }
        }
        ResponseResultType resultType;
        if (security == null) {
            resultType = ResponseResultType.json;
        } else {
            resultType = security.resultType();
        }
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
     * 封装封禁日志信息
     *
     * @param date
     * @param dbObject
     * @return
     */
    private static String buildDenyLogMessage(Date date, DBObject dbObject) {
        StringBuffer msg = new StringBuffer();
        msg.append(JodaTimeUtil.format(date, JodaTimeUtil.SECOND)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.IP)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.REGIONAL)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.COUNTRY)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.SUBVISION)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.CITY)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.DENY_START_TIME)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.DENY_END_TIME)).append(LOG_JOINER_STR);
        msg.append(dbObject.get(MongodbConstant.RATE)).append(LOG_JOINER_STR);
        msg.append(DENY_SET_CACHE);
        return msg.toString();
    }

    /**
     * 封装封禁日志信息
     *
     * @param cacheVal
     * @return
     */
    private static String buildDenyLogMsg(String cacheVal) {
        StringBuffer msg = new StringBuffer();
        DateTime nowDateTime = new DateTime();
        if (!Strings.isNullOrEmpty(cacheVal)) {
            String[] data = StringUtils.split(cacheVal, CACHE_VALUE_JOINER);
            msg.append(JodaTimeUtil.format(nowDateTime.toDate(), JodaTimeUtil.SECOND)).append(LOG_JOINER_STR);
            msg.append(data[1]).append(LOG_JOINER_STR);
            msg.append(data[2]).append(LOG_JOINER_STR);
            msg.append(data[3]).append(LOG_JOINER_STR);
            msg.append(data[4]).append(LOG_JOINER_STR);
            msg.append(data[5]).append(LOG_JOINER_STR);
            msg.append(data[6]).append(LOG_JOINER_STR);
            msg.append(data[7]).append(LOG_JOINER_STR);
            msg.append(data[8]).append(LOG_JOINER_STR);
            msg.append(DENY_CACHE_HIT);
        }
        return msg.toString();
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
