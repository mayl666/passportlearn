package com.sogou.upd.passport.service.account.mail;

import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SendCloud;
import com.sohu.sendcloud.SmtpApiHeader;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mayan
 * Date: 13-4-18
 * Time: 下午2:02
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = {"classpath:spring-config-mail.xml"})
public class TestSendEmail  extends AbstractJUnit4SpringContextTests {
     @Inject
     private SendCloud sendCloud;
  @Test
  public void send(){
     try{
       Message message = new Message("mayan@sogou.com", "Mayan Test");
       // 正文， 使用html形式，或者纯文本形式
       message.setBody(
           "Hi, %name%, 欢迎使用SendCloud") // html
           .setAltBody("Hi, %name%, 欢迎使用SendCloud"); // 纯文本

       // 添加to, cc, bcc replyto
       message.setSubject("SendCloud测试邮件");

       // X-SMTPAPI
       SmtpApiHeader smtpApiHeader = new SmtpApiHeader();
       // 添加category字段，只能添加一个
       smtpApiHeader.addCategory("xsmtpApi category");
       // 启动取消订阅， 打开跟踪，点击跟踪应用
//         smtpApiHeader.addFilter(AppFilter.ADD_UNSUBSCRIBE, "enable", "1");
//         smtpApiHeader.addFilter(AppFilter.ADD_HIDDEN_IMAGE, "enable", "1");
//         smtpApiHeader.addFilter(AppFilter.PROCESS_URL_REPLACE, "enable", "1");
       // add to
       List<String> toList = new ArrayList<String>();
       toList.add("mayan@sogou-inc.com");
//         toList.add("liuling@sogou-inc.com");

       smtpApiHeader.addRecipients(toList);

       //添加sub字段
       List<String>sub=new ArrayList<String>();
       sub.add("%bodyMale%");
       sub.add("%bodyFemale%");

       smtpApiHeader.addSub("%name%", sub);
       //添加section字段
//         smtpApiHeader.addSection("%bodyFemale%", "liuling");
       smtpApiHeader.addSection("%bodyMale%", "mayan");

       System.out.println(smtpApiHeader.toString());
       message.setXsmtpapiJsonStr(smtpApiHeader.toString());

       sendCloud.setMessage(message);
       sendCloud.send();

       // 获取emailId列表
       System.out.println(sendCloud.getEmailIdList());
     }catch (Exception e){
       e.printStackTrace();
     }

     }

//     @Test
//     public void setMailSender(){
//         SimpleMailMessage mail = new SimpleMailMessage(); //<span style="color: #ff0000;">注意SimpleMailMessage只能用来发送text格式的邮件</span>
//
//         try {
//             mail.setTo("34310327@qq.com");//接受者
//             mail.setFrom("Mayan");//发送者,这里还可以另起Email别名，不用和xml里的username一致
//             mail.setSubject("spring mail test!");//主题
//             mail.setText("springMail的简单发送测试");//邮件内容
//             mailSender.send(mail);
//         }catch (Exception e){
//         e.printStackTrace();
//         }
//     }
//
//    @Test
//    public void send(){
//            JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
//            // 设定mail server
//            senderImpl.setHost("smtp.163.com");
//
//            // 建立邮件消息
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            // 设置收件人，寄件人 用数组发送多个邮件
//            // String[] array = new String[] {"sun111@163.com","sun222@sohu.com"};
//            // mailMessage.setTo(array);
//            mailMessage.setTo("34310327@qq.com");
//            mailMessage.setFrom("testpassport22@163.com");
//            mailMessage.setSubject("你好 ");
//            mailMessage.setText("你好");
//
//            senderImpl.setUsername("testpassport22@163.com"); // 根据自己的情况,设置username
//            senderImpl.setPassword("qwaszx1"); // 根据自己的情况, 设置password
//
//            Properties prop = new Properties();
//            prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
//            prop.put("mail.smtp.timeout", "25000");
//            senderImpl.setJavaMailProperties(prop);
//            // 发送邮件
//            senderImpl.send(mailMessage);
//
//            System.out.println(" 邮件发送成功.. ");
//    }
}
