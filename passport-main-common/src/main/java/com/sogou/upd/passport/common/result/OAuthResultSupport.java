package com.sogou.upd.passport.common.result;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Passport-API的Result实现类
 */
public class OAuthResultSupport extends ResultSupport {

    private Logger log = LoggerFactory.getLogger(OAuthResultSupport.class);

    private String errorUri = CommonConstant.DEFAULT_INDEX_URL;

    /**
     * 创建一个result。
     */
    public OAuthResultSupport() {
    }

    /**
     * 创建一个result。
     *
     * @param success 是否成功
     */
    public OAuthResultSupport(boolean success) {
        super(success);
    }

    /**
     * 创建一个result。
     * 从ErrorUtil.ERR_CODE_MSG_MAP获取code对应错误信息
     *
     * @param success 是否成功
     */
    public OAuthResultSupport(boolean success, String code) {
        super(success);
        String message = ErrorUtil.ERR_CODE_MSG_MAP.get(code);
        setCode(code);
        setMessage(message);
    }

    public OAuthResultSupport(boolean success, String code, String message) {
        super(success, code, message);
    }

    public String getErrorUri() {
        return errorUri;
    }

    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    @Override
    public String toString() {
        Map resultMap = Maps.newHashMap();
        String result = "confirm";
        if (!isSuccess()) {
            result = "refuse";
            String error = getCode();
            String errorDescription = getMessage();
            if (Strings.isNullOrEmpty(errorDescription)) {
                errorDescription = ErrorUtil.ERR_CODE_MSG_MAP.get(error);
            }
            resultMap.put("error", error);
            resultMap.put("error_description", errorDescription);
            resultMap.put("error_uri", getErrorUri());
        } else {
            Map<String, Object> models = getModels();
            if (!models.isEmpty()) {
                for (String key : models.keySet()) {
                    if (!key.equals(DEFAULT_MODEL_KEY)) {
                        resultMap.put(key, models.get(key));
                    } else {
                        Object obj = models.get(DEFAULT_MODEL_KEY);
                        Map<String, Object> beanMap = BeanUtil.beanDescribe(obj);
                        resultMap.putAll(beanMap);
                    }
                }
            }
        }
        resultMap.put("result", result);

        try {
            return JacksonJsonMapperUtil.getMapper().writeValueAsString(resultMap);
        } catch (IOException e) {
            log.error("OAuth Result Object As String is error!");
            return "";
        }
    }

}