package com.sogou.upd.passport.common.model.httpclient;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.XMLUtil;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * 用于发送xml时使用，默认采用post方式
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午8:31
 */
public class RequestModelXml extends RequestModel {

    //root节点的名称
    private String rootNodeName;

    public void setRootNodeName(String rootNodeName) {
        if (StringUtil.isBlank(rootNodeName)) {
            throw new IllegalArgumentException("root node may not be null");
        }
        this.rootNodeName = rootNodeName;
    }

    public String getRootNodeName() {
        return this.rootNodeName;
    }

    /**
     * 初始化RequestModelXml
     *
     * @param url          要请求的参数
     * @param rootNodeName xml中Root节点的名称
     */
    public RequestModelXml(String url, String rootNodeName) {
        super(url);
        this.setRootNodeName(rootNodeName);
        this.setHttpMethodEnum(HttpMethodEnum.POST);
    }

    /**
     * 返回xml格式的参数
     *
     * @return
     */
    @Override
    public HttpEntity getRequestEntity() {
        String xmlStr = XMLUtil.mapToXmlString(this.rootNodeName, this.params);
        try {
            HttpEntity httpEntity = new StringEntity(xmlStr, "text/xml",
                    DEFAULT_ENCODE);
            return httpEntity;
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        }
    }
}
