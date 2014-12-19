package com.sogou.upd.passport.web;


import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.model.mobileoperation.*;
import net.sf.json.JSONArray;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class MobileOperationLogUtil {

    enum Type {
        INTERFACE("interfaceLogger"),
        EXCEPTION("exceptionLogger"),
        PRODUCT("productLogger"),
        DEBUGLOG("debugLogger"),
        ERRORLOG("errorLogger"),
        NETFLOW("netflowLogger");

        private String logName;

        Type(String logName) {
            this.logName = logName;
        }

        public String getLogName() {
            return logName;
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
        JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
        for (int i = 0; i < jsonArray.size(); i++) {
            Map map = JacksonJsonMapperUtil.getMapper().readValue(String.valueOf(jsonArray.get(i)), Map.class);
            if (MapUtils.isNotEmpty(map))
                logger.info(cinfo + "\t" + initMobileLog(type, map).toHiveString());
        }
    }

    /**
     * 初始化日志数据对象
     */
    public static MobileLog initMobileLog(String type, Map map) {
        MobileLog mobileLog = null;
        switch (type) {
            case "interface": {
                mobileLog = new InterfaceLog(map);
                break;
            }
            case "exception": {
                mobileLog = new ExceptionLog(map);
                break;
            }
            case "product": {
                mobileLog = new ProductLog(map);
                break;
            }
            case "debuglog": {
                mobileLog = new DebugLog(map);
                break;
            }
            case "errorlog": {
                mobileLog = new ErrorLog(map);
                break;
            }
            case "netflow": {
                mobileLog = new NetflowLog(map);
                break;
            }
            default: {
                break;
            }
        }
        return mobileLog;
    }


    /**
     * 初始化日志Log
     *
     * @param type
     */

    public static Logger initLogger(String type) {
        Logger logger = LoggerFactory.getLogger(Type.valueOf(type.toUpperCase()).getLogName());
        /*switch (type) {
            case "interface": {
                logger = LoggerFactory.getLogger("interfaceLogger");
                break;
            }
            case "exception": {
                logger = LoggerFactory.getLogger("exceptionLogger");
                break;
            }
            case "product": {
                logger = LoggerFactory.getLogger("productLogger");
                break;
            }
            case "debuglog": {
                logger = LoggerFactory.getLogger("debugLogger");
                break;
            }
            case "errorlog": {
                logger = LoggerFactory.getLogger("errorLogger");
                break;
            }
            case "netflow": {
                logger = LoggerFactory.getLogger("netflowLogger");
                break;
            }
            case "cinfo": {
                logger = LoggerFactory.getLogger("cinfoLogger");
                break;
            }
            default:
                break;
        }*/
        return logger;
    }
}
