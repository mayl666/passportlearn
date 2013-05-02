package com.sogou.upd.passport.common.utils;

import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SendCloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件工具类
 * User: mayan
 * Date: 13-4-18
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class MailUtils {
  private static Logger logger = LoggerFactory.getLogger(MailUtils.class);

  private static SendCloud sendCloud;

  public void sendEmail(Message message){
    try{
      sendCloud.setMessage(message);
      sendCloud.send();
    }catch (Exception e){
      logger.error("[SendEmail] send fail, email:"+message);
    }

  }

  public void setSendCloud(SendCloud sendCloud) {
    MailUtils.sendCloud = sendCloud;
  }
}
