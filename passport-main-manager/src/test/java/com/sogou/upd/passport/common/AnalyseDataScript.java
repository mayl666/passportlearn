package com.sogou.upd.passport.common;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-10-10
 * Time: 下午6:14
 * To change this template use File | Settings | File Templates.
 */
public class AnalyseDataScript extends BaseTest {

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Test
    public void test() throws Exception {

        File readFile = new File("d:/data.txt");
        BufferedReader reader1 = new BufferedReader(new FileReader(readFile));
        BufferedReader reader2 = new BufferedReader(new FileReader(readFile));
        File writeFile = new File("d:/dataresult.txt");
        BufferedWriter write = new BufferedWriter(new FileWriter(writeFile));
        try {
            String str = null;
            CheckUserApiParams params = new CheckUserApiParams();
            List<String> sohuList = Lists.newArrayList();
            List<String> sogouList = Lists.newArrayList();
            //检测出@sohu.com账号
            write.append("===以下账号应加@sohu.com后缀===").append("\r\n");
            int i = 0;
            int j = 0;
            while ((str = reader1.readLine()) != null) {
                String s = str + "@sohu.com";
                params.setUserid(s);
                Result result = proxyRegisterApiManager.checkUser(params);
                if (!result.isSuccess()) {
                    String userid = (String) result.getModels().get("userid");
                    write.append(str).append("\t").append(userid).append("\t").append(result.toString()).append("\r\n");
                    sohuList.add(str);
                    j++;
                }
                i++;
            }
            write.append("不能注册的账号量:" + j).append("\r\n");
            write.append("检测的账号总量:" + i).append("\r\n");
            //检测出@sogou.com账号
            write.append("===以下账号应加@sogou.com后缀===").append("\r\n");
            i = 0;
            j = 0;
            while ((str = reader2.readLine()) != null) {
                String s = str + "@sogou.com";
                params.setUserid(s);
                Result result = proxyRegisterApiManager.checkUser(params);
                if (!result.isSuccess()) {
                    String userid = (String) result.getModels().get("userid");
                    write.append(str).append("\t").append(userid).append("\t").append(result.toString()).append("\r\n");
                    sogouList.add(str);
                    j++;
                }
                i++;
            }
            write.append("不能注册的账号量:" + j).append("\r\n");
            write.append("检测的账号总量:" + i).append("\r\n");

            //检测出@sogou.com账号
            write.append("===以下账号@sogou.com、@sohu.com都有的===").append("\r\n");
            sohuList.retainAll(sogouList);
            for (String s : sohuList) {
                write.append(s).append("\r\n");
            }
            write.append("重复的账号量:" + sohuList.size()).append("\r\n");
        } finally {
            reader1.close();
            reader2.close();
            write.close();
        }
    }

}
