package com.sogou.upd.passport.web;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ApiGroupUtil;

import org.apache.commons.collections.MapUtils;
import org.apache.thrift.TException;
import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 用于记录用户行为的log
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-20
 * Time: 下午12:07
 */
public class UserOperationLogUtil {

    private static final Logger userOperationLogger = LoggerFactory.getLogger("userOperationLogger");

    private static final Logger logger = LoggerFactory.getLogger(UserOperationLogUtil.class);

    static {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        InputStream in = UserOperationLogUtil.class.getClassLoader().getResourceAsStream("scribe-logback.xml");

        try {
            configurator.doConfigure(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            if (StringUtil.isBlank(operation)) {
                operation = request.getRequestURI();
            }
            StringBuilder log = new StringBuilder();
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime()).substring(0, 10);
            log.append(timestamp);
            log.append(":").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
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

            userOperationLogger.info(log.toString());
        } catch (Exception e) {
            logger.error("UserOperationLogUtil.log error", e);
        }

    }
}
