package com.sogou.upd.passport.web.util;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import org.apache.commons.collections.MapUtils;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用于记录用户行为的log
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-20
 * Time: 下午12:07
 */
public class UserOperationLogUtil {

    private static final Logger userOperationLogger = LoggerFactory.getLogger("userOperationLogger");

    private static final Logger logger = LoggerFactory.getLogger(UserOperationLogUtil.class);


    /**
     * 记录用户行为
     * @param userOperationLog
     */
    public static void log(UserOperationLog userOperationLog){
        log(userOperationLog.getPassportId(),userOperationLog.getUserOperation(),userOperationLog.getClientId(),userOperationLog.getIp(), userOperationLog.getResultCode(),userOperationLog.getOtherMessageMap());
    }


    /**
     * 用于记录log代码
     *
     * @param passportId   用户id
     * @param operation    用户执行的操作
     * @param clientId     终端代码
     * @param resultCode   执行结果码
     * @param otherMessage 其它信息
     */
    public static void log(String passportId, String operation, String clientId, String ip, String resultCode,Map<String,String> otherMessage) {
        try{
            HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            if(StringUtil.isBlank(operation)){
                operation=request.getRequestURI();
            }
            StringBuilder log = new StringBuilder("passportId:");
            log.append(passportId);
            log.append(" ,operation:").append(operation);
            log.append(" ,clientId:").append(clientId);
            log.append(" ,ip:").append(ip);
            log.append(" ,resultCode:").append(resultCode);


            Object stopWatchObject=request.getAttribute( CommonConstant.STOPWATCH);
            if(stopWatchObject!=null&&stopWatchObject instanceof StopWatch){
                StopWatch stopWatch= (StopWatch) stopWatchObject;
                stopWatch.stop(request.getRequestURI());
                long costTime= stopWatch.getElapsedTime();
                log.append(" ,costTime:").append(costTime);
            }
            if(MapUtils.isNotEmpty(otherMessage)){
                for(Map.Entry<String,String> entry:otherMessage.entrySet()){
                    log.append(" ,").append(entry.getKey()).append(":").append(entry.getValue());
                }
            }
            userOperationLogger.info(log.toString());
        }catch (Exception e){
            logger.error("UserOperationLogUtil.log error",e);
        }
    }
}
