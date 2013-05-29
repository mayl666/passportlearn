package com.sogou.upd.passport.common.model;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.BeanUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 用于请求
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-28
 * Time: 下午2:06
 */
public class RequestModel {

    private static final String HEADER_CONTENT_TYPE="Content-Type";

    private static final String HEADER_CONTENT_TYPE_VALUE="application/x-www-form-urlencoded;charset=utf-8";

    private static final Logger logger = LoggerFactory.getLogger(RequestModel.class);

    //要请求的地址
    private String url;

    //请求的方法，默认采用post请求
    private HttpMethodEnum httpMethodEnum;

    //提交的参数
    private HttpParams httpParams;

    //提交的头信息
    private Map<String, String> headers;

    /**
     * @param url
     */
    public RequestModel(String url) {
        if (StringUtil.isBlank(url)) {
            throw new IllegalArgumentException("url不能为空！");
        }
        this.url = url;
        this.httpMethodEnum = HttpMethodEnum.GET;
        this.httpParams = new BasicHttpParams();
        this.headers=new HashMap<String,String>(1);
        this.headers.put(HEADER_CONTENT_TYPE,HEADER_CONTENT_TYPE_VALUE);
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
        this.headers=headers;
    }

    public void addHeader(String name,String value){
       this.headers.put(name,value);
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
        return this.httpParams.getParameter(key) != null;
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
        this.httpParams.setParameter(key, value);
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
        httpParams.removeParameter(key);
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
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            try {
                this.addParam(entry.getKey(), entry.getValue());
            } catch (IllegalArgumentException e) {
                logger.error("Object  to HttpParams exception", e);
            }
        }
    }

    public HttpParams getHttpParams(){
        return httpParams;
    }

    public Header[] getHeaders(){
        if(headers.isEmpty()){
           return new Header[0];
        }
        Header[] header=new Header[this.headers.size()];
        int i=0;
        for(Map.Entry<String,String> entry:this.headers.entrySet()){
            header[i]=new BasicHeader(entry.getKey(),entry.getValue());
            i++;
        }
        return header;
    }
}
