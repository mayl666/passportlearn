package com.sogou.upd.passport.web;


import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.model.mobileoperation.*;
import net.sf.json.JSONArray;
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

    public static Executor executor = Executors.newFixedThreadPool(10);


    /**
     * 记录日志
     *
     * @param type 日志类型
     * @param data 日志详情
     * @throws Exception
     */
    public static void log(String type, Map data, String cinfo) throws Exception {
        executor.execute(new LogTask(cinfo, type, data));
//        new LogTask(cinfo, type, data).run();
    }
}

class LogTask implements Runnable {

    private Logger log = LoggerFactory.getLogger(LogTask.class);

    private String cinfo;
    private String type;
    private Map data;
    private MobileLog mobileLog;

    public LogTask(String cinfo, String type, Map data) {
        this.cinfo = cinfo;
        this.type = type;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            Logger logger = LogTask.initLogger(type);
            JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
            for (int i = 0; i < jsonArray.size(); i++) {
                Map map = JacksonJsonMapperUtil.getMapper().readValue(String.valueOf(jsonArray.get(i)), Map.class);
                logger.info(cinfo + "\t" + this.initMobileLog(map).toHiveString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 初始化日志数据对象
     */
    public MobileLog initMobileLog(Map map) {
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
        Logger logger = null;
        switch (type) {
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
                logger = LoggerFactory.getLogger(MobileOperationLogUtil.class);
        }
        return logger;
    }
}
