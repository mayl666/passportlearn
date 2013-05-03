package com.sogou.upd.passport.oauth2.common.utils;

import com.sogou.upd.passport.oauth2.common.exception.HTMLTextParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HTMLTextUtils {

    public static Map<String, Object> parseHTMLText(String text) throws HTMLTextParseException {

        Map<String, Object> params = new HashMap<String, Object>();
        try {
            if (!StringUtils.isEmpty(text)) {
                String[] paramsArray = text.split("&");
                for (String value : paramsArray) {
                    String[] result = value.split("=");
                    params.put(result[0], result[1]);
                }
            }
        } catch (Exception e) {
            throw new HTMLTextParseException(e);
        }
        return params;
    }
}
