package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-10-10
 * Time: 下午6:14
 * To change this template use File | Settings | File Templates.
 */
public class T extends BaseTest {

    @Autowired
    private  RegisterApiManager proxyRegisterApiManager;
    @Test
    public void test() throws Exception{
        File file=new File("d:/1.txt");
        BufferedReader reader=new BufferedReader(new FileReader(file));

        String str=null;
        CheckUserApiParams params=new CheckUserApiParams();
//        params.setClient_id();
        while((str=reader.readLine())!=null){
            Result result=proxyRegisterApiManager.checkUser(params)  ;
        }
    }

}
