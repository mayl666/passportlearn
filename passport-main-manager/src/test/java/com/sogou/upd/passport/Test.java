package com.sogou.upd.passport;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.manager.ManagerHelper;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-25
 * Time: 下午9:12
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void  main(String args[]) throws Exception{
        long ct = System.currentTimeMillis();
        System.out.println("ct:" + ct);
//        String ct =  "1381915491000";
        String token ="7faada06773b30155f7eb93955845dfb";
        String code = ManagerHelper.generatorCodeGBK(token, 1115, "RBCqf6a448Wj5a8#KF&POL75*5GBQ5", ct);
        System.out.println("code:" + code);

        try {
            String pwdMD5 = Coder.encryptMD5("111111");
            System.out.println("pwdMD5:" + pwdMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
