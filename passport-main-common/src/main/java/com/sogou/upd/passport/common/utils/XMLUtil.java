package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.lang.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.bean.BeanElement;
import org.dom4j.tree.BaseElement;

import java.util.Map;

/**
 * 用于封装xml参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午9:08
 */
public class XMLUtil {

    /**
     * 将map转换为xml
     *
     * @param rootNode root节点的名称
     * @param map      要转换的map
     * @return
     */
    public static Document mapToXml(String rootNode, Map<String, Object> map) {
        if (StringUtil.isBlank(rootNode)) {
            throw new RuntimeException("xml rootNode may not be null");
        }
        Document document = DocumentHelper.createDocument();

        Element rootElement = document.addElement(rootNode);

        if (map == null || map.isEmpty()) {
            return document;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String value = entry.getValue() == null ? StringUtil.EMPTY_STRING : entry.getValue().toString();
            Element element = rootElement.addElement(entry.getKey());
            element.setText(value);
        }
        return document;
    }

    /**
     * 将map转换为xml
     *
     * @param rootNode root节点的名称
     * @param map      要转换的map
     * @return
     */
    public static String mapToXmlString(String rootNode, Map<String, Object> map) {
        Document document = mapToXml(rootNode, map);
        return document.asXML();
    }
}
