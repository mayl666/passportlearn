package com.sogou.upd.passport.web;


import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.ReflectUtil;
import com.sogou.upd.passport.model.mobileoperation.MobileBaseLog;
import com.sogou.upd.passport.web.account.action.mapp.TerminalAttributeDO;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class MobileOperationLogUtil {

    private static final Logger utilLogger = LoggerFactory.getLogger(MobileOperationLogUtil.class);
    private static final Logger interfaceLogger = LoggerFactory.getLogger("interfaceLogger");
    private static final Logger exceptionLogger = LoggerFactory.getLogger("exceptionLogger");
    private static final Logger productLogger = LoggerFactory.getLogger("productLogger");
    private static final Logger debugLogger = LoggerFactory.getLogger("debugLogger");
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");
    private static final Logger netflowLogger = LoggerFactory.getLogger("netflowLogger");

    enum Type {
        INTERFACE(interfaceLogger, "com.sogou.upd.passport.model.mobileoperation.InterfaceLog"),
        EXCEPTION(exceptionLogger, "com.sogou.upd.passport.model.mobileoperation.ExceptionLog"),
        PRODUCT(productLogger, "com.sogou.upd.passport.model.mobileoperation.ProductLog"),
        DEBUGLOG(debugLogger, "com.sogou.upd.passport.model.mobileoperation.DebugLog"),
        ERRORLOG(errorLogger, "com.sogou.upd.passport.model.mobileoperation.ErrorLog"),
        NETFLOW(netflowLogger, "com.sogou.upd.passport.model.mobileoperation.NetflowLog");

        private String className;   //type对应的对象类名
        private Logger logger;     //type对应的logger

        Type(Logger logger, String className) {
            this.logger = logger;
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        Logger getLogger() {
            return logger;
        }
    }

    /**
     * 记录日志
     *
     * @param type                日志类型
     * @param dataJson            日志详情
     * @param terminalAttributeDO
     * @throws Exception
     */
    public static void log(String type, String dataJson, TerminalAttributeDO terminalAttributeDO) {
        try {
            Map dataMap = JacksonJsonMapperUtil.getMapper().readValue(dataJson, Map.class);
            Logger logger = Type.valueOf(type).getLogger();
            if (!MapUtils.isEmpty(dataMap)) {
                Object obj = dataMap.get("data");
                if (obj instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                    for (Map<String, Object> valueMap : list) {
                        logger.info(terminalAttributeDO.toHiveString() + initMobileLog(type, valueMap).toHiveString());
                    }
                } else if (obj instanceof Map) {
                    Map valueMap = (Map) obj;
                    logger.info(terminalAttributeDO.toHiveString() + initMobileLog(type, valueMap).toHiveString());
                } else {
                    utilLogger.error("mobileOperationLog is not list or map!type:" + type);
                }
            }
        } catch (Exception e) {
        }
//        JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
    }

    /**
     * 初始化日志数据对象
     */
    public static MobileBaseLog initMobileLog(String type, Map map) throws Exception {
        MobileBaseLog mobileBaseLog = null;
        String className = Type.valueOf(type).getClassName();
        try {
            Class clazz = Class.forName(className);
            mobileBaseLog = (MobileBaseLog) ReflectUtil.instantiateClassWithParameters(clazz, new Class[]{Map.class}, new Object[]{map});
            return mobileBaseLog;
        } catch (ClassNotFoundException e) {
            utilLogger.error("Instantiate Class With Parameters NoSuchMethodException! Class:" + className, e);
            throw e;
        }
    }
}
