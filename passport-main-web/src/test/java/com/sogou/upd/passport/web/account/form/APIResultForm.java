package com.sogou.upd.passport.web.account.form;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class APIResultForm {
    private Logger log = LoggerFactory.getLogger(APIResultForm.class);

    private Map<String, Object> data = Maps.newHashMap();
    private String status;
    private String statusText;

    public APIResultForm() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APIResultForm that = (APIResultForm) o;

        if (!data.equals(that.data)) return false;
        if (!status.equals(that.status)) return false;
        if (!statusText.equals(that.statusText)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + statusText.hashCode();
        return result;
    }

    public static void main(String args[]) {
        APIResultForm form = new APIResultForm();
        form.setStatus("0");
    }
}
