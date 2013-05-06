package com.sogou.upd.passport.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * User: mayan Date: 13-5-6 Time: 下午4:54 To change this template use File | Settings | File
 * Templates.
 */
public class BaseService {

  private Properties props = null;

  public Properties getProps() {
    if (props == null) {
      props = new Properties();
      props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
      props.setProperty(Velocity.RESOURCE_LOADER, "class");
      props.setProperty("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }
    return props;
  }

  public String getMailBody(String fileName, Map<String, Object> objectMap) throws Exception {
    VelocityEngine ve = new VelocityEngine();
    // 取得velocity的模版
    ve.init(getProps());
    //取得velocity的模版
    Template t = ve.getTemplate(fileName, "utf-8");
    VelocityContext context = new VelocityContext();
    // 输出流
    StringWriter writer = new StringWriter();
    if (objectMap != null && objectMap.size() > 0) {
      Set<String> key = objectMap.keySet();
      for (Iterator it = key.iterator(); it.hasNext(); ) {
        String k = (String) it.next();
        context.put(k, objectMap.get(k));
      }
      // 转换输出
      t.merge(context, writer);
    }
    return writer.toString();
  }
}
