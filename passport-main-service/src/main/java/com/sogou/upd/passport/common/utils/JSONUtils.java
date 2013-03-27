package com.sogou.upd.passport.common.utils;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * User: mayan
 * Date: 13-3-27
 * Time: 下午6:04
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtils {
    static Logger logger = LoggerFactory.getLogger(JSONUtils.class);
    static final ObjectMapper objectMaper = new ObjectMapper();

    static {
        objectMaper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
        objectMaper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
    }
    /**
     * 对象到json转换
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMaper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("serialize exception!", e);
        }
        return null;
    }
    /**
     * json到对象转换
     */
    public static <T> T jsonToObject(String content, Class<T> claz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        try {
            return objectMaper.readValue(content, claz);
        } catch (Exception e) {
            logger.error("deserialize exception!", e);
        }
        return null;
    }

    public static String buildJSON(Map<String, Object> params) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey() != null && !"".equals(param.getKey()) && param.getValue() != null
                    && !"".equals(param.getValue())) {
                jsonObject.put(param.getKey(), param.getValue());
            }
        }

        return jsonObject.toString();
    }

    public static Map<String, Object> parseJSONObject(String jsonBody) throws JSONException {

        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject obj = JSONObject.fromObject(jsonBody);
        Iterator<?> it = obj.keys();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof String) {
                String key = (String) o;
                params.put(key, obj.get(key));
            }

        }
        return params;
    }
}
