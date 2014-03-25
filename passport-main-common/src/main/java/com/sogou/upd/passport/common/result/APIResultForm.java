package com.sogou.upd.passport.common.result;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-25
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class APIResultForm {
    private Logger log = LoggerFactory.getLogger(APIResultForm.class);

    private Map<String, Object> data = Maps.newHashMap();
    private String status;
    private String statusText;

    public APIResultForm(){

    }

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
            str = JacksonJsonMapperUtil.getMapper().writeValueAsString(this);
        } catch (IOException e) {
            log.error("ResultObject As String is error!");
        }
        return str;
    }

    public static void main(String args[]){
        APIResultForm form = new APIResultForm();
        form.setStatus("0");
    }
}
