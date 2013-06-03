package com.sogou.upd.passport.common.model.httpclient;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.BeanUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * 用于请求
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-28
 * Time: 下午2:06
 */
public class RequestModel {

    protected static final String DEFAULT_ENCODE = "UTF-8";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded;charset=utf-8";

    protected static final Logger logger = LoggerFactory.getLogger(RequestModel.class);

    //要请求的地址
    private String url;

    //请求的方法，默认采用post请求
    private HttpMethodEnum httpMethodEnum;

    //提交的参数
    protected Map<String, Object> params;

    //提交的头信息
    private Map<String, String> headers;

    /**
     * @param url
     */
    public RequestModel(String url) {
        if (StringUtil.isBlank(url)) {
            throw new IllegalArgumentException("url不能为空！");
        }
        this.url = url.trim();
        this.httpMethodEnum = HttpMethodEnum.GET;
        this.params = new HashMap<String, Object>();
        this.headers = new HashMap<String, String>(1);
        this.headers.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
    }

    public HttpMethodEnum getHttpMethodEnum() {
        return httpMethodEnum;
    }

    public void setHttpMethodEnum(HttpMethodEnum httpMethodEnum) {
        this.httpMethodEnum = httpMethodEnum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * 检查是否存在参数
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        return this.params.containsKey(key);
    }

    /**
     * 增加参数，如果原key已经存在一个value，则覆盖老参数
     *
     * @param key   参数名
     * @param value 参数值
     */
    public void addParam(String key, Object value) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        if (value == null || StringUtil.isBlank(value.toString())) {
            throw new IllegalArgumentException("value 不能为空");
        }
        this.params.put(key, value);
    }

    /**
     * 删除参数
     *
     * @param key
     */
    public void deleteParams(String key) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        params.remove(key);
    }

    /**
     * 把整个对象转换为map参数
     *
     * @param object
     */
    public void addParams(Object object) {
        Map<String, Object> param = BeanUtil.beanDescribe(object);
        if (param == null || param.isEmpty()) {
            return;
        }
        this.params.putAll(param);
    }

    public Header[] getHeaders() {
        if (headers.isEmpty()) {
            return new Header[0];
        }
        Header[] header = new Header[this.headers.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            header[i] = new BasicHeader(entry.getKey(), entry.getValue());
            i++;
        }
        return header;
    }

    public HttpEntity getRequestEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(this.params.size());
        for (Map.Entry<String, Object> entry : this.params.entrySet()) {
            NameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            nameValuePairs.add(param);
        }
        try {
            return new UrlEncodedFormEntity(nameValuePairs, DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        }
    }
}
