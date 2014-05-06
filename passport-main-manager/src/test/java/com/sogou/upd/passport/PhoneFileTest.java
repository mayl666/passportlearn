package com.sogou.upd.passport;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
            List<String> phoneList = FileIOUtil.readFileByLines("D:\\statis_phone.txt");
            FileOutputStream fos = new FileOutputStream("D:/phone_passportid");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            int i = 0, j = 0;
            BufferedWriter bw = new BufferedWriter(osw);
            for (String phone : phoneList) {
                BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
                baseMoblieApiParams.setMobile(phone);
                Result result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
                if (result.isSuccess()) {
                    String passportId = (String) result.getModels().get("userid");
                    i++;
                    bw.write(phone + " " + passportId + "\n");

                }
                j++;
            }
            System.out.println("passportid total:" + i);
            System.out.println("total:" + j);
        } catch (IOException e) {
        }
    }
}
