package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
public class AccountInfoManagerTest extends BaseTest {
    @Autowired
    private AccountInfoManager accountInfoManager;


    @Test
    public void testUpload() {
        File file=new File("d:/1.jpg");
        try {
            Result result=accountInfoManager.uploadImg(IOUtils.toByteArray(new FileInputStream(file)),"mayan@sogou.com","0") ;
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testObtainImage() {
        Result result=accountInfoManager.obtainPhoto("mayan@sogou.com","180");
        System.out.println("result:"+result.getModels().get("180"));
    }


}
