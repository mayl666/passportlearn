package com.sogou.upd.passport.web;

import com.sogou.upd.passport.manager.ManagerHelper;

import org.junit.Test;

/**
 * Created by wanghuaqing on 2016/12/1.
 */
public class DemoTest {
    @Test
    public void test() {
        String firstStr = "j417351@mvrht.com";
        int clientId = 1120;
        String secret = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        long ct = 1580414752554L;
        
        String code = ManagerHelper.generatorCode(firstStr, clientId, secret, ct);
        System.err.println(code);
    }
}
