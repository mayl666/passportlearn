package com.sogou.upd.passport.web.internal.connect.file;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.ConnectTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.AccountInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-26
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class SecureInfoToAccountThread implements Runnable {
    private CountDownLatch latch;
    private String fileName;
    private UserInfoApiManager proxyUserInfoApiManager;
    private AccountInfoService accountInfoService;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public SecureInfoToAccountThread(CountDownLatch latch, String fileName, UserInfoApiManager proxyUserInfoApiManager, AccountInfoService accountInfoService) {
        this.latch = latch;
        this.fileName = fileName;
        this.proxyUserInfoApiManager = proxyUserInfoApiManager;
        this.accountInfoService = accountInfoService;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        String logOpenId = null;
        try {
            long start = System.currentTimeMillis();
            File file = new File(this.fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String[] rowString = tempString.split(",");
                String openIdString = rowString[4];  //openid
                logOpenId = openIdString;
                String passportIdString = AccountTypeEnum.generateThirdPassportId(openIdString, rowString[3]);
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(passportIdString);
                getUserInfoApiparams.setClient_id(CommonConstant.SGPP_DEFAULT_CLIENTID);
                long ct = System.currentTimeMillis();
                getUserInfoApiparams.setCt(ct);
                String code = ManagerHelper.generatorCode(passportIdString, CommonConstant.SGPP_DEFAULT_CLIENTID, CommonConstant.SGPP_DEFAULT_SERVER_SECRET, ct);
                getUserInfoApiparams.setCode(code);
                String fields = "birthday,gender,province,city,personalid,username";
                getUserInfoApiparams.setFields(fields);
                Result result = new APIResultSupport(false);
                try {
                    result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                } catch (Exception e) {
                    //1.获取第三方用户信息失败，记录格式为passportId,provider,appKey,openId,accessToken,date
                    FileWriter writer = new FileWriter("D:\\transfer\\info_exception.txt", true);
                    writer.write(passportIdString + ",get user info exception:" + result.toString());
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (result.isSuccess()) {
                    Map<String, String> map = (Map<String, String>) result.getModels().get("data");
                    String birthday = map.get("birthday").toString();
                    String gender = map.get("gender").toString();
                    String province = map.get("province").toString();
                    String city = map.get("city").toString();
                    String personalid = map.get("personalid").toString();
                    String username = map.get("username").toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(birthday);
                    AccountInfo accountInfo = new AccountInfo();
                    accountInfo.setBirthday(date);
                    accountInfo.setCity(city);
                    accountInfo.setFullname(username);
                    accountInfo.setGender(gender);
                    accountInfo.setPersonalid(personalid);
                    accountInfo.setProvince(province);
                    accountInfo.setPassportId(passportIdString);
                    boolean isInsertSuccess = accountInfoService.updateAccountInfo(accountInfo);
                    if(!isInsertSuccess){
                        //1.获取第三方用户信息失败，记录格式为passportId,provider,appKey,openId,accessToken,date
                        FileWriter writer = new FileWriter("D:\\transfer\\insert_failed.txt", true);
                        writer.write(passportIdString + ",insert into account_info error");
                        writer.write("\r\n");
                        writer.close();
                        continue;
                    }
                } else {
                    //1.获取第三方用户信息失败，记录格式为passportId,provider,appKey,openId,accessToken,date
                    FileWriter writer = new FileWriter("D:\\transfer\\info_failed.txt", true);
                    writer.write(passportIdString + ",error info:" + result.toString());
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
            }
            reader.close();
            System.out.println(Thread.currentThread().getName() + ":" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error("出错记录passportId为：" + logOpenId, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    logger.error("异常信息：", e1);
                }
            }
            latch.countDown();
        }
    }
}
