package com.sogou.upd.passport.common.utils;

import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SendCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 邮件工具类
 * User: mayan
 * Date: 13-4-18
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class MailUtils {
    private static Logger logger = LoggerFactory.getLogger(MailUtils.class);
    private static final String FROM_ADDRESS = "postmaster@sogou-upd-passport.sendcloud.org";
    private static final String FROM_NAME = "搜狗通行证";


    private static SendCloud sendCloud;



    public void sendEmail(Message message) throws Exception{
        try {
            sendCloud.setMessage(message);
            sendCloud.send();
        } catch (Exception e) {
            logger.error("[SendEmail] send fail", e);
            throw new Exception();
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

    public void setSendCloud(SendCloud sendCloud) {
        MailUtils.sendCloud = sendCloud;
    }
}
