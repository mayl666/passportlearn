package com.sogou.upd.passport.web;


import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class MobileOperationLogUtil {

    private static Logger logger = null;

    /**
     * 初始化日志Log
     *
     * @param type
     */
    private static void init(String type) {
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
    }

    /**
     * 记录日志
     *
     * @param type 日志类型
     * @param data 日志详情
     * @throws Exception
     */
    public static void log(String type, Map data) throws Exception {
        try {
            init(type);
            JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
            for (int i = 0; i < jsonArray.size(); i++) {
                logger.info(jsonArray.get(i) + "");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
