package com.sogou.upd.passport.result;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.BeanUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Passport-API的Result实现类
 */
public class APIResultSupport extends ResultSupport {

    private Logger log = LoggerFactory.getLogger(APIResultSupport.class);

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
     *
     * @param success 是否成功
     */
    public APIResultSupport(boolean success, ResultCode resultCode) {
        super(success, resultCode);
    }

    @Override
    public String toString() {
        String status = "0";
        String statusText = "";
        if (!isSuccess()) {
            status = getResultCode().getMap().getMessageID();
            statusText = getResultCode().getMap().getMessageData();
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

    class APIResultForm {

        private Map<String, Object> data = Maps.newHashMap();
        private String status;
        private String statusText;

        public APIResultForm(String status, String statusText, Map<String, Object> data) {
            this.status = status;
            this.statusText = statusText;
            this.data = data;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        @Override
        public String toString() {
            String str = "";
            try {
                str = new ObjectMapper().writeValueAsString(this);
            } catch (IOException e) {
                log.error("ResultObject As String is error!");
            }
            return str;
        }
    }
}
