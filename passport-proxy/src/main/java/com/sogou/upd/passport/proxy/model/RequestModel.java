package com.sogou.upd.passport.proxy.model;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.common.model.BaseApiParams;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import java.util.HashMap;
import java.util.Map;


/**
 * 用于请求
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-28
 * Time: 下午2:06
 */
public class RequestModel {

    //要请求的地址
    private String url;

    //请求的方法，默认采用post请求
    private HttpMethodEnum httpMethodEnum;

    //XML的root节点的名称，默认为 info 目前文档中只有注册的接口使用的是 register
    private String rootNode;

    //提交的参数
    private Map<String,Object> params;

    //提交的头信息
    private Map<String, String> headers;

    /**
     *
     * @param url
     */
    private RequestModel(String url){
        if(StringUtil.isBlank(url)){
            throw new IllegalArgumentException("url不能为空！");
        }
        this.url=url;
        this.httpMethodEnum=HttpMethodEnum.POST;
        this.rootNode="info";
        params=new HashMap<String,Object>();
    }

    public String getRootNode() {
        return rootNode;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 检查是否存在参数
     * @param key
     * @return
     */
    public boolean containsKey(String key){
        return params.containsKey(key);
    }

    /**
     * 增加参数，如果原key已经存在一个value，则覆盖老参数
     * @param key 参数名
     * @param value 参数值
     */
    private void addParams(String key,Object value){
        this.params.put(key,value);
    }

    /**
     * 删除参数
     * @param key
     */
    public void deleteParams(String key){
        this.params.remove(key);
    }

    /**
     * 把整个对象转换为map参数
     * @param object
     */
    public <T extends BaseApiParams> void addParams(T object){
        Map<String, Object> param= BeanUtil.beanDescribe(object);
        this.params.putAll(param);
    }
}
