package com.sogou.upd.passport;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.PhoneUtil;
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

    /**
     * 输入：手机号
     * 输出：主账号userid
     */
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

    /**
     * 输入：手机号+手机号@sohu.com
     * 输出：注册的手机号的userid
     */
    @Test
    public void testBuildRegisterPhoneUserid() {
        try {
            List<String> phoneList = FileIOUtil.readFileByLines("D:\\全量迁移需要的用户列表\\5.7_all_sgpp_phone_userid.txt");
            FileOutputStream fos = new FileOutputStream("D:\\全量迁移需要的用户列表\\5.7_all_sgpp_regphone_userid.txt");
            int i = 0, j = 0;
            for (String phone : phoneList) {
                String outputStr;
                if (phone.contains("@sohu.com")) {
                    outputStr = phone + "\n";
                    fos.write(outputStr.getBytes());
                } else {
                    BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
                    baseMoblieApiParams.setMobile(phone);
                    Result result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
                    if (result.isSuccess()) {
                        String passportId = (String) result.getModels().get("userid");
                        if (passportId.equals(phone + "@sohu.com")) {
                            i++;
                            outputStr = passportId + "\n";
                            fos.write(outputStr.getBytes());
                        }
                    } else {
                        outputStr = phone + "@sohu.com" + "\n";
                        fos.write(outputStr.getBytes());
                    }
                }
                j++;
            }
            System.out.println("regPhone total:" + i);
            System.out.println("total:" + j);
        } catch (IOException e) {
        }
    }

    /**
     * 输入：外域账号+手机号+手机号@sohu.com
     * 输出：外域账号+注册的手机号的userid
     */
    @Test
    public void testBuildRegisterPhoneOtherUserid() {
        try {
            List<String> phoneList = FileIOUtil.readFileByLines("D:\\全量迁移需要的用户列表\\all-userid");
            FileOutputStream fos = new FileOutputStream("D:\\全量迁移需要的用户列表\\4.30_sgproduct_regphone_other_userid");
            int i = 0, j = 0;
            for (String phone : phoneList) {
                String outputStr;
                if (!PhoneUtil.verifyPhoneNumberFormat(phone)) {
                    outputStr = phone + "\n";
                    fos.write(outputStr.getBytes());
                } else {
                    BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
                    baseMoblieApiParams.setMobile(phone);
                    Result result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
                    if (result.isSuccess()) {
                        String passportId = (String) result.getModels().get("userid");
                        if (passportId.equals(phone + "@sohu.com")) {
                            i++;
                            outputStr = passportId + "\n";
                            fos.write(outputStr.getBytes());
                        }
                    } else {
                        outputStr = phone + "@sohu.com" + "\n";
                        fos.write(outputStr.getBytes());
                    }
                }
                j++;
            }
            System.out.println("regPhone total:" + i);
            System.out.println("total:" + j);
        } catch (IOException e) {
        }
    }

    private static List<String> sgproductList = Lists.newArrayList();

    static {
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-daohang");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-game.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-haha");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-map.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-open.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-se.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-shurufapc.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-shurufaweb.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-shurufawap.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-yingyongshichang");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-youxihezi.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-yuedu.txt");
        sgproductList.add("D:\\全量迁移需要的用户列表\\sgproduct-userid\\other-phone-user-zhanzhang.txt");
    }

    /**
     * 输入：外域账号+手机号+手机号@sohu.com
     * 输出：外域账号+注册的手机号的userid
     */
    @Test
    public void testChajiInSgproduct() {
        try {
            List<String> chajiUseridList = FileIOUtil.readFileByLines("D:\\全量迁移需要的用户列表\\4.30_sgproduct_regphone_other_userid_uniq_0521");
            FileOutputStream fos = new FileOutputStream("D:\\全量迁移需要的用户列表\\chaji_in_sgproduct");
            for (String sgproductUrl : sgproductList) {
                List<String> useridList = FileIOUtil.readFileByLines(sgproductUrl);
                int i = 0, j = 0;
                for (String chajiUserid : chajiUseridList) {
                    for (String str : useridList) {
                        if (str.contains(chajiUserid)) {
                            String outputStr = sgproductUrl + " " + str + "\n";
                            fos.write(outputStr.getBytes());
                        }
                        i++;
                    }
                    j++;
                }
                System.out.println(sgproductUrl + " same total:" + i);
                System.out.println(sgproductUrl + " total:" + j);
            }
        } catch (IOException e) {
        }
    }
}
