package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.exception.MailException;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SendCloud;
import com.sohu.sendcloud.SmtpApiHeader;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 邮件工具类
 * User: mayan
 * Date: 13-4-18
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class MailUtils {

    public static final long MAX_EMAIL_COUNT_ONEDAY = 10; // 每天发送邮件次数

    private static Logger logger = LoggerFactory.getLogger(MailUtils.class);
    private static final String FROM_ADDRESS = "postmaster@sogou-upd-passport.sendcloud.org";
    private static final String FROM_NAME = "搜狗通行证";


    private static SendCloud sendCloud;
    private static Properties props = null;

    public static Properties getProps() {
      if (props == null) {
        props = new Properties();
        props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        props.setProperty(Velocity.RESOURCE_LOADER, "class");
        props.setProperty("class.resource.loader.class",
                          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      }
      return props;
    }


    public void sendEmail(ActiveEmail activeEmail) throws MailException{
        try {
          String templateFile=activeEmail.getTemplateFile();
          Map<String,Object> map=activeEmail.getMap();
          String subject=activeEmail.getSubject();
          String category=activeEmail.getCategory();
          String toEmail=activeEmail.getToEmail();
          //发送邮件
          Message message=getMessage();

          String mailBody=getMailBody(templateFile,map);
          // 正文， 使用html形式，或者纯文本形式
          message.setBody(mailBody);
          message.setSubject(subject);

          // X-SMTPAPI
          SmtpApiHeader smtpApiHeader = new SmtpApiHeader();
          smtpApiHeader.addCategory(category);
          smtpApiHeader.addRecipient(toEmail);

          message.setXsmtpapiJsonStr(smtpApiHeader.toString());

          sendCloud.setMessage(message);
          sendCloud.send();
        } catch (Exception e) {
            logger.error("[SendEmail] send fail", e);
            throw new MailException();
        }
    }

    public Message getMessage() {
        Message message = null;
        try {
            message = new Message(FROM_ADDRESS, FROM_NAME);
        } catch (Exception e) {
            logger.error("[SendEmail] getMessage fail" + e);
        }
        return message;
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


    public void setSendCloud(SendCloud sendCloud) {
        MailUtils.sendCloud = sendCloud;
    }
}
