package com.sogou.upd.passport.web;


import com.sogou.upd.passport.model.mobileoperation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    enum Type {
        INTERFACE("interfaceLogger", "com.sogou.upd.passport.model.mobileoperation.InterfaceLog"),
        EXCEPTION("exceptionLogger", "com.sogou.upd.passport.model.mobileoperation.ExceptionLog"),
        PRODUCT("productLogger", "com.sogou.upd.passport.model.mobileoperation.ProductLog"),
        DEBUGLOG("debugLogger", "com.sogou.upd.passport.model.mobileoperation.DebugLog"),
        ERRORLOG("errorLogger", "com.sogou.upd.passport.model.mobileoperation.ErrorLog"),
        NETFLOW("netflowLogger", "com.sogou.upd.passport.model.mobileoperation.NetflowLog");

        private String logName;
        private String className;

        Type(String logName, String className) {
            this.logName = logName;
            this.className = className;
        }

        public String getLogName() {
            return logName;
        }

        public String getClassName() {
            return className;
        }

    }

    /**
     * 记录日志
     *
     * @param type 日志类型
     * @param data 日志详情
     * @throws Exception
     */
    public static void log(String type, Map data, String cinfo) throws Exception {
        Logger logger = initLogger(type);
        Object obj = data.get("data");
        if (obj instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
            for (int i = 0; i < list.size(); i++) {
                logger.info(cinfo + "\t" + initMobileLog(type, list.get(i)).toHiveString());
            }
        } else {
            Map map = (Map) obj;
            logger.info(cinfo + "\t" + initMobileLog(type, map).toHiveString());
        }
//        JSONArray jsonArray = JSONArray.fromObject(data.get("data"));

    }

    /**
     * 初始化日志数据对象
     */
    public static MobileLog initMobileLog(String type, Map map) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String className = Type.valueOf(type.toUpperCase()).getClassName();
        Class clazz = Class.forName(className);
        Constructor constructor = clazz.getConstructor(Map.class);
        MobileLog mobileLog = (MobileLog) constructor.newInstance(map);
        return mobileLog;
    }


    /**
     * 初始化日志Log
     *
     * @param type
     */

    public static Logger initLogger(String type) {
        String loggerName = Type.valueOf(type.toUpperCase()).getLogName();
        Logger logger = LoggerFactory.getLogger(loggerName);
        return logger;
    }
}
