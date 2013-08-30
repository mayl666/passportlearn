package com.sogou.upd.passport.web;

import com.google.common.base.Strings;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ApiGroupUtil;

import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于记录用户行为的log
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-20
 * Time: 下午12:07
 */
public class UserOperationLogUtil {

    private static final Logger userLoggerScribe = LoggerFactory.getLogger("userLoggerScribe");
    private static final Logger userLoggerBase = LoggerFactory.getLogger("userLoggerBase");
    private static final Logger userLoggerLocal = LoggerFactory.getLogger("userLoggerLocal");
    private static final Logger userLoggerAsync = LoggerFactory.getLogger("userLoggerAsync");


    private static final Logger logger = LoggerFactory.getLogger(UserOperationLogUtil.class);

    private static String LOCALIP = null;

    private static Logger userLogger = userLoggerScribe;

/*    private static AmqpAdmin admin;
    private static RabbitTemplate template;
    private static CachingConnectionFactory connectionFactory;

    static {
        connectionFactory = new CachingConnectionFactory("10.146.32.57", 5672);
        template = new RabbitTemplate(connectionFactory);
        //template.setQueue("passport_user");
        template.setQueue("passport_user_1");
        admin = new RabbitAdmin(connectionFactory);
    }*/

    private static ConnectionFactory connectionFactory =  new ConnectionFactory();
    private static Connection connection;
    private static Channel channel;
    static {
        connectionFactory.setHost("10.146.32.57");
        connectionFactory.setPort(5672);
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("passport_user_1", false, false, false, null);
        } catch (Exception e){

        }
    }

    public static void setUserLogger(String flag) {
        if ("scribe".equals(flag)) {
            userLogger = userLoggerScribe;
        } else if ("local".equals(flag)) {
            userLogger = userLoggerLocal;
        } else if ("async".equals(flag)) {
            userLogger = userLoggerAsync;
        } else {
            userLogger = userLoggerBase;
        }
    }

    /**
     * 记录用户行为
     *
     * @param userOperationLog
     */
    public static void log(UserOperationLog userOperationLog) {
        log(userOperationLog.getPassportId(), userOperationLog.getUserOperation(), userOperationLog.getClientId(), userOperationLog.getIp(), userOperationLog.getResultCode(), userOperationLog.getOtherMessageMap());
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
    public static void log(String passportId, String operation, String clientId, String ip, String resultCode, Map<String, String> otherMessage) {
        try {
            long start = System.currentTimeMillis();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            if (StringUtil.isBlank(operation)) {
                operation = request.getRequestURI();
            }
            StringBuilder log = new StringBuilder();
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime()).substring(0, 10);
            log.append(timestamp);
            log.append(":").append(new SimpleDateFormat("HH:mm:ss").format(date));

            log.append("\t").append(StringUtil.defaultIfEmpty(getLocalIp(request), "-"));

            log.append("\t").append(StringUtil.defaultIfEmpty(passportId, "-"));

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
            log.append("\t").append(StringUtil.defaultIfEmpty(ApiGroupUtil.getApiGroup(operation), "-"));
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

            String referer = otherMessage.remove("ref");
            log.append("\t").append(StringUtil.defaultIfEmpty(referer, "-"));

            String otherMsgJson = new ObjectMapper().writeValueAsString(otherMessage).replace("\t", " ");
            log.append("\t").append(otherMsgJson);

            // Thread.sleep(60);
            start = System.currentTimeMillis();
            userLogger.info(log.toString());
            // template.convertAndSend(log.toString());
/*            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("passport_user_1", false, false, false, null);
            channel.basicPublish("", "passport_user_1", null, log.toString().getBytes());
            channel.queueDeclare("passport_user", false, false, false, null);
            channel.basicPublish("", "passport_user", null, log.toString().getBytes());*/
            log.append(System.currentTimeMillis() - start);
            userLoggerBase.info(log.toString()); //TODO
        } catch (Exception e) {
            logger.error("UserOperationLogUtil.log error", e);
        }

    }

    private static String getLocalIp(HttpServletRequest request) {
        try {
            if (Strings.isNullOrEmpty(LOCALIP)) {
                LOCALIP = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
            LOCALIP = request.getLocalAddr();
        }

        return LOCALIP;
    }
}
