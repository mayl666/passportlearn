package com.sogou.upd.passport;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-5-6
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class PhoneFileTest extends BaseTest {

    @Autowired
    private BindApiManager proxyBindApiManager;

    @Test
    public void testQueryPassportIdByPhone() {
        try {
            List<String> phoneList = FileIOUtil.readFileByLines("D:\\statis_phone_201404.txt");
            FileOutputStream fos = new FileOutputStream("D:\\statis_phone_bindpassportid_201404.txt");
            int i = 0, j = 0;
            for (String str : phoneList) {
                String[] strArray = str.split("\\t");
                String phone = strArray[0];
                String clientId = strArray[1];
                String date = strArray[2];
                BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
                baseMoblieApiParams.setMobile(phone);
                Result result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
                if (result.isSuccess()) {
                    String passportId = (String) result.getModels().get("userid");
                    try {
                        if (!passportId.equals(phone + "@sohu.com") && passportId.contains("@sohu.com")) {
                            i++;
                            String outputStr = phone + " " + clientId + " " + date + " " + passportId + "\n";
                            fos.write(outputStr.getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                j++;
            }
            System.out.println("passportid total:" + i);
            System.out.println("total:" + j);
        } catch (IOException e) {
        }
    }
}
