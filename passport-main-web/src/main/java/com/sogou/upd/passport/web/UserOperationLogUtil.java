package com.sogou.upd.passport.web;


import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.common.hystrix.HystrixKafkaSemaphoresCommand;
import com.sogou.upd.passport.common.hystrix.HystrixKafkaThreadCommand;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ApiGroupUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import org.apache.commons.collections.MapUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 用于记录用户行为的log
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-20
 * Time: 下午12:07
 */
public class UserOperationLogUtil {

    private static final Logger userOperationLogger = LoggerFactory.getLogger("userLoggerAsync");
    private static final Logger userOperationLocalLogger = LoggerFactory.getLogger("userLoggerLocal");
    private static Logger userLogger = userOperationLogger;
    private static final Logger hystrixLogger = LoggerFactory.getLogger("hystrixLogger");
//    private static final Logger hystrixCostPerfLogger= LoggerFactory.getLogger("hystrixCostPerfLogger");

    private static final int SLOW_TIME=10;//10ms

    //把useLogger分离开：local+kafka
    private static Logger userLocalLogger = LoggerFactory.getLogger("userLoggerLocal");
    private static Logger userKafkaLogger = LoggerFactory.getLogger("userLoggerKafka");

    private static String NEXTLINE = "%0A"; // \n换行符的UTF-8编码
    private static String TAB = "%09"; // \t制表符的UTF-8编码

    private static final Logger logger = LoggerFactory.getLogger(UserOperationLogUtil.class);

    private static String LOCALIP = null;

    /**
     * 修改Logger，“local”修改为只记本地，“mq/rabbitmq”修改为传RabbitMQ和本地
     *
     * @param loggerName local/mq/rabbitmq
     */
    public static void changeLogger(String loggerName) {
        if ("local".equalsIgnoreCase(loggerName)) {
            userLogger = userOperationLocalLogger;
        } else if ("mq".equalsIgnoreCase(loggerName) || "rabbitmq".equalsIgnoreCase(loggerName)) {
            userLogger = userOperationLogger;
        } else {
            //
        }
    }

    /**
     * 记录用户行为
     *
     * @param userOperationLog
     */
    public static void log(UserOperationLog userOperationLog) {
        log(userOperationLog.getPassportId(), userOperationLog.getUserOperation(), userOperationLog.getClientId(), userOperationLog.getIp(), userOperationLog.getResultCode(), userOperationLog.getOtherMessageMap(), userLogger);
    }

    public static void log(UserOperationLog userOperationLog, Logger authEmailUserLogger) {
        log(userOperationLog.getPassportId(), userOperationLog.getUserOperation(), userOperationLog.getClientId(), userOperationLog.getIp(), userOperationLog.getResultCode(), userOperationLog.getOtherMessageMap(), authEmailUserLogger);
    }

    /**
     * 用于记录log代码
     * 日志格式：日期+时间  用户ID  用户域  用户操作  应用ID  IP地址  结果代码  响应时间  referer  附加信息
     *
     * @param passportId   用户id
     * @param operation    用户执行的操作
     * @param clientId     终端代码
     * @param resultCode   执行结果码
     * @param otherMessage 其它信息
     */
    public static void log(String passportId, String operation, String clientId, String ip, String resultCode, Map<String, String> otherMessage, Logger operationLogger) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            if (StringUtil.isBlank(operation) && request != null) {
                operation = request.getRequestURI();
            }
            StringBuilder log = new StringBuilder();
            Date date = new Date();
            log.append(new SimpleDateFormat("yyy-MM-dd_HH:mm:ss").format(date));
            log.append("\t").append(StringUtil.defaultIfEmpty(getLocalIp(request), "-"));
            log.append("\t").append(StringUtil.defaultIfEmpty(passportId, "-").replace("\t", TAB).replace("\n", NEXTLINE));  // 防止恶意用户调用接口输入非法用户名

            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            String domainStr = domain.toString();
            if (domain == AccountDomainEnum.THIRD) {
                AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(passportId);
                if (accountTypeEnum != AccountTypeEnum.UNKNOWN) {
                    domainStr = accountTypeEnum.toString();
                }
            } else if (domain == AccountDomainEnum.INDIVID) {
                if (operation.indexOf("connect") != -1) {
                    domainStr = passportId;
                } else {
                    domainStr = AccountDomainEnum.SOGOU.toString();
                }
            }

            log.append("\t").append(StringUtil.defaultIfEmpty(domainStr, "-"));
            log.append("\t").append(StringUtil.defaultIfEmpty(operation, "-"));
            log.append("\t").append(StringUtil.defaultIfEmpty(ApiGroupUtil.getApiGroup(operation), "others"));
            log.append("\t").append(StringUtil.defaultIfEmpty(clientId, "-"));
            log.append("\t").append(StringUtil.defaultIfEmpty(ip, "-"));
            log.append("\t").append(StringUtil.defaultIfEmpty(resultCode, "-"));

            Object stopWatchObject = request.getAttribute(CommonConstant.STOPWATCH);
            if (stopWatchObject != null && stopWatchObject instanceof StopWatch) {
                StopWatch stopWatch = (StopWatch) stopWatchObject;
                long startTime = stopWatch.getStartTime();
                long costTime = System.currentTimeMillis() - startTime;
                log.append("\t").append(costTime);
            } else {
                log.append("\t-");
            }

            if (MapUtils.isEmpty(otherMessage)) {
                log.append("\t-\t-");
            } else {
                String referer = otherMessage.remove("ref");
                log.append("\t").append(StringUtil.defaultIfEmpty(referer, "-").replace("\t", TAB).replace("\n", NEXTLINE));

                String otherMsgJson = null;
                if (MapUtils.isNotEmpty(otherMessage)) {
                    otherMsgJson = JacksonJsonMapperUtil.getMapper().writeValueAsString(otherMessage).replace("\t", TAB).replace("\n", NEXTLINE);
                }
                log.append("\t").append(StringUtil.defaultIfEmpty(otherMsgJson, "-"));
            }
            log.append("\t").append(StringUtil.defaultIfEmpty(request.getHeader("X-Http-Real-Port"), "-"));
//            userLogger.info(log.toString());

            userLocalLogger.info(log.toString());
            //userKafkaLogger.info(log.toString());
            //调用hystrix 线程隔离kafka command
//            hystrixLogger.warn("UserOperationLogUtil invoke hystrix...");
            Boolean hystrixGlobalEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_GLOBAL_ENABLED));
//            StopWatch stopWatch = new Slf4JStopWatch(hystrixCostPerfLogger);
            if (hystrixGlobalEnabled) {
                new HystrixKafkaThreadCommand(log.toString()).execute();
//                new HystrixKafkaSemaphoresCommand(log.toString()).execute();
            } else{
                userKafkaLogger.info(log.toString());
            }
//            stopWatch(stopWatch,"hystrix_kafka_cost","success");


        } catch (Exception e) {
            logger.error("UserOperationLogUtil.log error", e);
        }
    }
//
//    private static void stopWatch(StopWatch stopWatch, String tag, String message) {
//        //无论什么情况都记录下所有的请求数据
//        if (stopWatch.getElapsedTime() >= SLOW_TIME) {
//            tag += "(slow)";
//        }
//        stopWatch.stop(tag, message);
//    }

    private static String getLocalIp(HttpServletRequest request) {
        try {
            if (Strings.isNullOrEmpty(LOCALIP)) {
                LOCALIP = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
            if (request != null) {
                LOCALIP = request.getLocalAddr();
            }
        }

        return LOCALIP;
    }
}
