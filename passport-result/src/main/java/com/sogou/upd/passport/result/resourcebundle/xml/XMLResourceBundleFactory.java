package com.sogou.upd.passport.result.resourcebundle.xml;

import java.io.InputStream;

import com.sogou.upd.passport.result.resourcebundle.*;
import com.sogou.upd.passport.result.excepiton.ResourceBundleCreateException;
import com.sogou.upd.passport.result.resourcebundle.proxy.AutoReloadResourceBundleProxy;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * 从XML文件中创建<code>ResourceBundle</code>的实例的工厂.
 *
 * User: shipengzhi
 * Date: 13-5-24
 * Time: 上午1:14
 */
public class XMLResourceBundleFactory extends AbstractResourceBundleFactory {
    /**
     * 创建factory, 使用当前线程的context class loader作为bundle装入器.
     */
    public XMLResourceBundleFactory() {
        super();
    }

    /**
     * 创建factory, 使用指定的class loader作为bundle装入器.
     *
     * @param classLoader 装入bundle的class loader
     */
    public XMLResourceBundleFactory(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * 创建factory, 使用指定的loader作为bundle装器
     *
     * @param loader bundle装入器
     */
    public XMLResourceBundleFactory(ResourceBundleLoader loader) {
        super(loader);
    }

    /**
     * 根据bundle的名称取得resource的文件名称.
     *
     * @param bundleName bundle的名称
     *
     * @return resource的名称
     */
    protected String getFilename(String bundleName) {
        return super.getFilename(bundleName) + ResourceBundleConstant.RB_RESOURCE_EXT_XML;
    }

    /**
     * 以XML格式解析输入流, 并创建<code>ResourceBundle</code>.
     *
     * @param stream 输入流
     * @param systemId 标志输入流的字符串
     *
     * @return resource bundle
     *
     * @throws ResourceBundleCreateException 如果解析失败
     */
    protected ResourceBundle parse(InputStream stream, String systemId)
            throws ResourceBundleCreateException {
        try {
            ResourceBundle resourceBundle;
            SAXReader reader = new SAXReader();
            Document  doc = reader.read(stream, systemId);
            resourceBundle = new XMLResourceBundle(doc);
            //判断是否需要自动重加载
            if(ResourceBundleFactory.isAutoReload()){
                String bundleName = systemId.substring(0, systemId.length() - ResourceBundleConstant.RB_RESOURCE_EXT_XML.length());
                resourceBundle = new AutoReloadResourceBundleProxy(resourceBundle, this, bundleName, ResourceBundleFactory.getCheckModifyInterval());
            }
            return resourceBundle;
        } catch (DocumentException e) {
            throw new ResourceBundleCreateException(ResourceBundleConstant.RB_FAILED_READING_XML_DOCUMENT,
                    new Object[] { systemId }, e);
        }
    }
}

