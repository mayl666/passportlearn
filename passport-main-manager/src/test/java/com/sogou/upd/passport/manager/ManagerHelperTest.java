package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */
public class ManagerHelperTest extends BaseTest {

    @Test
    public void testGeneratorCode() {
        long ct = System.currentTimeMillis();
        System.out.println("ct:" + ct);
        String code = ManagerHelper.generatorCode(userid, clientId, serverSecret, ct);
        System.out.println("code:" + code);

        /*try {
            String pwdMD5 = Coder.encryptMD5(code);
            System.out.println("pwdMD5:" + pwdMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Test
    public void testCheck() {
        String str = "232_4e3e_";
        //只含有汉字、数字、字母、下划线，且不能以下划线开头和结尾
        System.out.println(str.matches("^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$"));
        //限制输入含有特定字符的
        //System.out.println(str.matches("^(?!.*搜狗)(?!.*sogou)(?!.*sougou).*$"));
    }
}
