package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 下午2:08
 */
public class ProxyRegisterApiManagerImplTest extends BaseTest {

    private static final String MOBILE = "18738963584";
    private static final String USERID = "dfsfs234232212@qq.com";
    private static final String PASSWORD = "111111";

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
  @Autowired
  private LoginApiManager proxyLoginApiManager;
  @Autowired
  private SecureApiManager proxySecureApiManager;

    @Test
    public void testRegMailUser() {
        long start=System.currentTimeMillis();
        RegEmailApiParams params = new RegEmailApiParams();
        params.setUserid(USERID);
        params.setPassword(PASSWORD);
        params.setRu("http://wan.sogou.com");
        params.setCreateip("10.1.164.65");
        Result result = proxyRegisterApiManager.regMailUser(params);
        System.out.println(result);
      System.out.println(System.currentTimeMillis()-start);
    }

    @Test
    public void testSendMobileRegCaptcha() {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(MOBILE);
        Result result = proxyRegisterApiManager.sendMobileRegCaptcha(baseMoblieApiParams);
        System.out.println(result);
    }

    @Test
    public void testRegMobileCaptchaUser() {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setMobile(MOBILE);
        regMobileCaptchaApiParams.setPassword(PASSWORD);
        regMobileCaptchaApiParams.setCaptcha("1540");
        Result result = proxyRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
        System.out.println(result);
    }

    @Test
    public void testCheckUser() {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("test_lg_upd@sogou.com");
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("lg-coder@sogou.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertFalse(result.isSuccess());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("13621009174@sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

//    @Test
//    public void tt(){
//      try {
//        List<Long> list=new CopyOnWriteArrayList<>();
//        CountDownLatch latch = new CountDownLatch(5);
//        for (int i=0;i<5;i++){
//          String userid="dfsadasd13"+i;
//          executor.execute(new T(latch,list,userid,proxySecureApiManager));
//        }
//
//        latch.await();
//        long avg=0;
//        for (int i=0;i<list.size();i++){
//          avg= (long)list.get(i) +avg;
//          System.out.println((long)list.get(i));
//        }
//        System.out.println("avg:"+avg/5);
//      }catch (Exception e){}
//      finally {
//        executor.shutdown();
//      }
//    }
}

