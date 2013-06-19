package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class ProblemManagerImplTest extends BaseTest {

    @Autowired
    private ProblemManager problemManagerImpl;


    private static final int clientId = 1100;
    private static final String username = "18600369478";
    private static final String ip = "192.168.226.174";
    private static final String pwd = "123456";
//    private static String passpword = Coder.encryptMD5("spz1986411");
    @Test
    public void testAccountLogin() {
        try {
            WebAddProblemParameters webAddProblemParameters = new WebAddProblemParameters();
            webAddProblemParameters.setPassportId("18612532596@sohu.com");
            webAddProblemParameters.setContent("搜狗通行证很好");
            webAddProblemParameters.setEmail("jiamengchen@126.com");
            webAddProblemParameters.setTitle("标题党");
            webAddProblemParameters.setTypeId("266");
            Result result = problemManagerImpl.insertProblem(webAddProblemParameters,ip);
            System.out.println("testAccountLogin:"+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
