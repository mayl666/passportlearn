package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.web.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-7-1 Time: 下午12:36 To change this template use
 * File | Settings | File Templates.
 */
@Controller
public class TestAPIAction extends BaseController {
  @Autowired
  private RegisterApiManager proxyRegisterApiManager;
  @Autowired
  private LoginApiManager proxyLoginApiManager;
  @Autowired
  private SecureApiManager proxySecureApiManager;
  private static ExecutorService executor = Executors.newFixedThreadPool(5);
//  private static final Logger logger = LoggerFactory.getLogger("com.sogou.upd.passport.regBlackListFileAppender");
  org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.sogou.upd.passport.loginBlackListFileAppender");


  @RequestMapping(value = "/test/reg", method = RequestMethod.GET)
  @ResponseBody
  public String testReg(){
    try {
      List<Long> list=new CopyOnWriteArrayList<>();
      CountDownLatch latch = new CountDownLatch(5);
      for (int i=0;i<5;i++){
        String userid="dfsadasd14"+i;
        executor.execute(new T(latch,list,userid,proxySecureApiManager));
      }

      latch.await();
      long avg=0;
      for (int i=0;i<list.size();i++){
        avg= (long)list.get(i) +avg;
        logger.info("reg:"+(long)list.get(i));
      }
      logger.info("reg avg:"+avg/5);
    }catch (Exception e){}
    finally {
      executor.shutdown();
    }
    return null;
  }

  @RequestMapping(value = "/test/pwd", method = RequestMethod.GET)
  @ResponseBody
  public String testPwd(){
    try {

      long startTime=System.currentTimeMillis();
      UpdatePwdApiParams updatePwdApiParams=new UpdatePwdApiParams();
      updatePwdApiParams.setUserid("dfsadasd130@sogou.com");
      updatePwdApiParams.setModifyip("192.168.1.1");
      updatePwdApiParams.setPassword("111111");
      updatePwdApiParams.setNewpassword("111111");
      Result result= proxySecureApiManager.updatePwd(updatePwdApiParams);
      System.out.println(result.toString());
      long endTime=System.currentTimeMillis()-startTime;
      logger.info("#########:"+endTime);



      List<Long> list=new CopyOnWriteArrayList<>();
      CountDownLatch latch = new CountDownLatch(5);
      for (int i=0;i<5;i++){
        String userid="dfsadasd13"+i;
        executor.execute(new T(latch,list,userid,proxySecureApiManager));
      }

      latch.await();
      long avg=0;
      for (int i=0;i<list.size();i++){
        avg= (long)list.get(i) +avg;
        logger.info(":"+(long)list.get(i));
      }
      logger.info("update avg:"+avg/5);
    }catch (Exception e){}
    finally {
      executor.shutdown();
    }
    return null;
}
class T implements Runnable{
  CountDownLatch latch = null;
  List<Long> list;
  SecureApiManager proxySecureApiManager;
  String userid;

  T(CountDownLatch latch,List<Long> list,String userid,SecureApiManager proxySecureApiManager) {
    this.proxySecureApiManager=proxySecureApiManager;
    this.userid=userid;
    this.list=list;
    this.latch = latch;
  }

  @Override
  public void run() {
    try {
      long startTime=System.currentTimeMillis();
      UpdatePwdApiParams updatePwdApiParams=new UpdatePwdApiParams();
      updatePwdApiParams.setUserid(userid+"@sogou.com");
      updatePwdApiParams.setModifyip("192.168.1.1");
      updatePwdApiParams.setPassword("111111");
      updatePwdApiParams.setNewpassword("111111");
      Result result= proxySecureApiManager.updatePwd(updatePwdApiParams);
      System.out.println(result.toString());
      long endTime=System.currentTimeMillis()-startTime;
      list.add(endTime);

      latch.countDown();
    }catch (Exception e){}
  }
}}
