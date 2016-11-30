package com.sogou.upd.passport.common.parameter;


import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 账号类型，第三方，邮箱，手机号码
 *
 * @author shipengzhi
 */
public class MappStatReportType {
    private static final Logger interfaceLogger = LoggerFactory.getLogger("interfaceLogger");
    private static final Logger exceptionLogger = LoggerFactory.getLogger("exceptionLogger");
    private static final Logger productLogger = LoggerFactory.getLogger("productLogger");
    private static final Logger debugLogger = LoggerFactory.getLogger("debugLogger");
    private static final Logger netflowLogger = LoggerFactory.getLogger("netflowLogger");

    private static final Map<String, Type> TYPE_MAP = Maps.newHashMap();

    static {
        TYPE_MAP.put("interface", Type.INTERFACE);
        TYPE_MAP.put("exception", Type.EXCEPTION);
        TYPE_MAP.put("product", Type.PRODUCT);
        TYPE_MAP.put("debuglog", Type.DEBUGLOG);
        TYPE_MAP.put("netflow", Type.NETFLOW);
    }

    public static boolean isSupportType(String typeName) {
        return TYPE_MAP.containsKey(typeName);
    }

    public static Logger getLogger(String typeName) {
        return TYPE_MAP.get(typeName).getLogger();
    }

    public static String getClassName(String typeName) {
        return TYPE_MAP.get(typeName).getClassName();
    }

    private enum Type {
        INTERFACE(interfaceLogger, "com.sogou.upd.passport.model.mobileoperation.InterfaceLog"), //接口响应
        EXCEPTION(exceptionLogger, "com.sogou.upd.passport.model.mobileoperation.ExceptionLog"),  //sdk异常
        PRODUCT(productLogger, "com.sogou.upd.passport.model.mobileoperation.ProductLog"), //产品
        DEBUGLOG(debugLogger, "com.sogou.upd.passport.model.mobileoperation.DebugLog"), //普通日志
        NETFLOW(netflowLogger, "com.sogou.upd.passport.model.mobileoperation.NetflowLog"); //流量

        private Logger logger;     //type对应的logger
        private String className;   //type对应的对象类名

        private Type(Logger logger, String className) {
            this.logger = logger;
            this.className = className;
        }

        private String getClassName() {
            return className;
        }

        private Logger getLogger() {
            return logger;
        }
    }
}
