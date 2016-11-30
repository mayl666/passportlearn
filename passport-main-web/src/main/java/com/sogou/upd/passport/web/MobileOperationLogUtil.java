package com.sogou.upd.passport.web;


import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.MappStatReportType;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.ReflectUtil;
import com.sogou.upd.passport.model.mobileoperation.MobileBaseLog;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     * 记录日志
     *
     * @param typeName          日志类型
     * @param dataJson          日志详情
     * @param terminalAttribute
     */
    public static void log(String typeName, String dataJson, TerminalAttribute terminalAttribute, String userIp) {
        try {
            Map dataMap = JacksonJsonMapperUtil.getMapper().readValue(dataJson, Map.class);
            if (!MapUtils.isEmpty(dataMap)) {
                Logger logger = MappStatReportType.getLogger(typeName);
                Object obj = dataMap.get("data");
                if (obj instanceof List) {
                    List<String> list = (List<String>) obj;
                    for (String valuejson : list) {
                        Map valueMap = JacksonJsonMapperUtil.getMapper().readValue(valuejson, Map.class);
                        log(logger, terminalAttribute, initMobileLog(typeName, valueMap), userIp);
                    }
                } else if (obj instanceof String) {
                    String valuejson = (String) obj;
                    Map valueMap = JacksonJsonMapperUtil.getMapper().readValue(valuejson, Map.class);
                    log(logger, terminalAttribute, initMobileLog(typeName, valueMap), userIp);
                } else {
                    utilLogger.error("mobileOperationLog is not list or map!type:" + typeName);
                }
            }
        } catch (Exception e) {
            utilLogger.error("mobileOperationLog parse error!", e);
        }
//        JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
    }

    /**
     * 初始化日志数据对象
     */
    public static <T extends MobileBaseLog> T initMobileLog(String typeName, Map map) throws Exception {
        String className = MappStatReportType.getClassName(typeName);
        try {
            Class clazz = Class.forName(className);
            T mobileLog = (T) ReflectUtil.instantiateClassWithParameters(clazz, new Class[]{Map.class}, new Object[]{map});
            return mobileLog;
        } catch (ClassNotFoundException e) {
            utilLogger.error("Instantiate Class With Parameters NoSuchMethodException! Class:" + className, e);
            throw e;
        }
    }

    /**
     * 用于记录log代码
     * 日志格式：日期+时间  终端属性 各类型移动端数据
     */
    public static <T extends MobileBaseLog> void log(Logger operationLogger, TerminalAttribute terminalAttribute, T mobileLog, String userIp) {
        StringBuilder log = new StringBuilder();
        Date date = new Date();
        log.append(new SimpleDateFormat("yyy-MM-dd_HH:mm:ss").format(date));
        log.append("\t").append(StringUtil.defaultIfEmpty(userIp, "-"));
        if (null != terminalAttribute) {
            log.append("\t").append(String.valueOf(terminalAttribute.toHiveString()));
        } else {
            log.append("\t").append(String.valueOf(new TerminalAttribute().toHiveString()));
        }
        log.append("\t").append(mobileLog.toHiveString());
        operationLogger.info(log.toString());
    }
}
