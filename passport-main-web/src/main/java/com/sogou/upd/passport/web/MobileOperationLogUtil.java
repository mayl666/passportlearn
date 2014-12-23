package com.sogou.upd.passport.web;


import com.sogou.upd.passport.common.parameter.MappStatReportType;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.ReflectUtil;
import com.sogou.upd.passport.model.mobileoperation.MobileBaseLog;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
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

    /**
     * 记录日志
     *
     * @param typeName              日志类型
     * @param dataJson          日志详情
     * @param terminalAttribute
     */
    public static void log(String typeName, String dataJson, TerminalAttribute terminalAttribute) {
        try {
            Map dataMap = JacksonJsonMapperUtil.getMapper().readValue(dataJson, Map.class);
            if (!MapUtils.isEmpty(dataMap)) {
                Logger logger = MappStatReportType.getLogger(typeName);
                Object obj = dataMap.get("data");
                if (obj instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                    for (Map<String, Object> valueMap : list) {
                        logger.info(terminalAttribute.toHiveString() + initMobileLog(typeName, valueMap).toHiveString());
                    }
                } else if (obj instanceof Map) {
                    Map valueMap = (Map) obj;
                    logger.info(terminalAttribute.toHiveString() + initMobileLog(typeName, valueMap).toHiveString());
                } else {
                    utilLogger.error("mobileOperationLog is not list or map!type:" + typeName);
                }
            }
        } catch (Exception e) {
            utilLogger.error("mobileOperationLog parse error!data=" + dataJson);
        }
//        JSONArray jsonArray = JSONArray.fromObject(data.get("data"));
    }

    /**
     * 初始化日志数据对象
     */
    public static MobileBaseLog initMobileLog(String typeName, Map map) throws Exception {
        String className = MappStatReportType.getClassName(typeName);
        try {
            Class clazz = Class.forName(className);
            MobileBaseLog mobileBaseLog = (MobileBaseLog) ReflectUtil.instantiateClassWithParameters(clazz, new Class[]{Map.class}, new Object[]{map});
            return mobileBaseLog;
        } catch (ClassNotFoundException e) {
            utilLogger.error("Instantiate Class With Parameters NoSuchMethodException! Class:" + className, e);
            throw e;
        }
    }
}
