package com.sogou.upd.passport.common.result;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
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
public class APIResultSupport extends ResultSupport {

    /**
     * 创建一个result。
     */
    public APIResultSupport() {
    }

    /**
     * 创建一个result。
     *
     * @param success 是否成功
     */
    public APIResultSupport(boolean success) {
        super(success);
    }

    /**
     * 创建一个result。
     * 从ErrorUtil.ERR_CODE_MSG_MAP获取code对应错误信息
     *
     * @param success 是否成功
     */
    public APIResultSupport(boolean success, String code) {
        super(success);
        String message = ErrorUtil.ERR_CODE_MSG_MAP.get(code);
        setCode(code);
        setMessage(message);
    }

    public APIResultSupport(boolean success, String code, String message) {
        super(success, code, message);
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
        if (success) {
            this.code = "0";
        }
    }

    @Override
    public String toString() {
        String status = "0";
        String statusText = getMessage();
        if (!isSuccess()) {
            status = getCode();
            if (Strings.isNullOrEmpty(statusText)) {
                statusText = ErrorUtil.ERR_CODE_MSG_MAP.get(status);
            }
        }
        Map<String, Object> models = getModels();
        Map<String, Object> data = Maps.newHashMap();
        if (!models.isEmpty()) {
            for (String key : models.keySet()) {
                if (!key.equals(DEFAULT_MODEL_KEY)) {
                    data.put(key, models.get(key));
                } else {
                    Object obj = models.get(DEFAULT_MODEL_KEY);
                    Map<String, Object> beanMap = BeanUtil.beanDescribe(obj);
                    data.putAll(beanMap);
                }
            }
        }
        APIResultForm form = new APIResultForm(status, statusText, data);
        return form.toString();
    }

}
