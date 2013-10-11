package com.sogou.upd.passport.common;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
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
        BufferedReader reader = new BufferedReader(new FileReader(readFile));
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
            while ((str = reader.readLine()) != null) {
                params.setUserid(str + "@sohu.com");
                Result result = proxyRegisterApiManager.checkUser(params);
                if (!result.isSuccess()) {
                    String userid = (String) result.getModels().get("userid");
                    write.append(str).append("\t").append(userid).append("\r\n");
                    sohuList.add(str);
                }
                i++;
            }
            write.append("total:" + i).append("\r\n");
            //检测出@sogou.com账号
            write.append("===以下账号应加@sogou.com后缀===").append("\r\n");
            while ((str = reader.readLine()) != null) {
                params.setUserid(str + "@sogou.com");
                Result result = proxyRegisterApiManager.checkUser(params);
                if (result.getCode().equals(ErrorUtil.ERR_CODE_USER_ID_EXIST)) {
                    String userid = (String) result.getModels().get("userid");
                    write.append(str).append("\t").append(userid).append("\r\n");
                    sogouList.add(str);
                }
                i++;
            }
            write.append("total:" + i).append("\r\n");

            //检测出@sogou.com账号
            write.append("===以下账号@sogou.com、@sohu.com都有的===").append("\r\n");
            sohuList.retainAll(sogouList);
            for (String s : sohuList) {
                write.append(s).append("\r\n");
            }
        } finally {
            reader.close();
            write.close();
        }
    }

}
