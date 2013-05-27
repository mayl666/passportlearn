package com.sogou.upd.passport.result;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.ClassUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * 默认的result实现
 */
public class ResultSupport implements Result {

    private static final long serialVersionUID = 3976733653567025460L;
    private boolean success = true;
    private ResultCode resultCode;
    private Map<String, Object> models = Maps.newHashMap();
    private String defaultModelKey;

    /**
     * 创建一个result。
     */
    public ResultSupport() {
    }

    /**
     * 创建一个result。
     *
     * @param success 是否成功
     */
    public ResultSupport(boolean success) {
        this.success = success;
    }

    /**
     * 创建一个result。
     *
     * @param success 是否成功
     */
    public ResultSupport(boolean success, ResultCode resultCode) {
        this.success = success;
        this.resultCode = resultCode;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public ResultCode getResultCode() {
        return resultCode;
    }

    @Override
    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getDefaultModelKey() {
        return StringUtils.defaultIfEmpty(defaultModelKey, DEFAULT_MODEL_KEY);
    }

    @Override
    public Object getDefaultModel() {
        return models.get(getDefaultModelKey());
    }

    @Override
    public void setDefaultModel(Object model) {
        setDefaultModel(DEFAULT_MODEL_KEY, model);
    }

    @Override
    public void setDefaultModel(String key, Object model) {
        defaultModelKey = StringUtils.defaultIfEmpty(key, DEFAULT_MODEL_KEY);
        models.put(key, model);
    }

    @Override
    public Map getModels() {
        return models;
    }

    /**
     * 转换成字符串的表示。
     *
     * @return 字符串表示
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Result {\n");
        buffer.append(" success = ").append(success).append(",\n");
        buffer.append(" resultCode = ").append(resultCode).append(",\n");
        buffer.append(" models = {");

        if (models.isEmpty()) {
            buffer.append("}\n");
        } else {
            buffer.append("\n");
            for (Iterator i = models.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                Object key = entry.getKey();
                Object value = entry.getValue();

                buffer.append(" ").append(key).append(" = ");

                if (value != null) {
                    buffer.append("(").append(ClassUtil.getClassNameForObject(value)).append(") ");
                }
                buffer.append(value);
                if (i.hasNext()) {
                    buffer.append(",");
                }
                buffer.append("\n");
            }
            buffer.append(" }\n");
        }
        buffer.append("}");
        return buffer.toString();
    }
}
