package com.sogou.upd.passport.web.internal.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
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
    private AccountDAO accountDAO;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public SecureInfoToAccountThread(CountDownLatch latch, String fileName, UserInfoApiManager proxyUserInfoApiManager, AccountInfoService accountInfoService, AccountDAO accountDAO) {
        this.latch = latch;
        this.fileName = fileName;
        this.proxyUserInfoApiManager = proxyUserInfoApiManager;
        this.accountInfoService = accountInfoService;
        this.accountDAO = accountDAO;
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
                //构建调用sohu接口的参数类
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(passportIdString);
                getUserInfoApiparams.setClient_id(CommonConstant.SGPP_DEFAULT_CLIENTID);
                long ct = System.currentTimeMillis();
                getUserInfoApiparams.setCt(ct);
                String code = ManagerHelper.generatorCode(passportIdString, CommonConstant.SGPP_DEFAULT_CLIENTID, CommonConstant.SGPP_DEFAULT_SERVER_SECRET, ct);
                getUserInfoApiparams.setCode(code);
                String fields = "birthday,gender,province,city,personalid,username,createip,createtime";
                getUserInfoApiparams.setFields(fields);
                Result result = new APIResultSupport(false);
                try {
                    result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                } catch (Exception e) {
                    //从sohu获取第三方用户信息异常
                    FileWriter writer = new FileWriter("D:\\transfer\\account_info\\get_from_sohu_exception.txt", true);
                    writer.write(passportIdString + ",get user info exception info is :" + result.toString());
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (result.isSuccess()) {
                    Map<String, String> map = (Map<String, String>) result.getModels();
                    String birthday = map.get("birthday");
                    String gender = map.get("gender");
                    String province = map.get("province");
                    String city = map.get("city");
                    String personalid = map.get("personalid");
                    String username = map.get("username");
                    String createIp = map.get("createip");
                    String createTime = map.get("createtime");
                    //创建时间
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date_createtime;

                    if (!Strings.isNullOrEmpty(createTime)) {
                        try {
                            date_createtime = sdf1.parse(createTime);
                        } catch (Exception e) {
                            //记录下来create_time格式错误的记录
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\create_time_null.txt", true);
                            writer.write(passportIdString + ",connect user create_time format error : createTime is " + createTime);
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                    } else {
                        //记录下来create_time为空的记录
                        FileWriter writer = new FileWriter("D:\\transfer\\account_info\\create_time_null.txt", true);
                        writer.write(passportIdString + ",connect user create_time in sohu is null");
                        writer.write("\r\n");
                        writer.close();
                        continue;
                    }
                    //生日
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date_birthday = null;
                    if (!Strings.isNullOrEmpty(birthday)) {
                        try {
                            date_birthday = sdf2.parse(birthday);
                        } catch (Exception e) {
                            //记录下来create_time为空的记录
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\create_time_null.txt", true);
                            writer.write(passportIdString + ",connect user birthday format error : birthday is " + birthday);
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                    }
                    //构建写account_info表的类
                    AccountInfo accountInfo = new AccountInfo();
                    accountInfo.setBirthday(date_birthday); //生日
                    accountInfo.setCity(city);      //城市
                    accountInfo.setFullname(username); //真实姓名
                    accountInfo.setGender(gender);    //性别
                    accountInfo.setPersonalid(personalid);  //身份证号
                    accountInfo.setProvince(province);    //省
                    accountInfo.setPassportId(passportIdString);  //passportId
                    accountInfo.setCreateTime(new Date());    //account_info表中记录创建时间
                    accountInfo.setUpdateTime(new Date());    //account_info表中记录更新时间
                    boolean isInsertSuccess;
                    try {
                        isInsertSuccess = accountInfoService.updateAccountInfo(accountInfo);
                    } catch (Exception e) {
                        //插入到account_info表异常
                        FileWriter writer = new FileWriter("D:\\transfer\\account_info\\insert_account_info_exception.txt", true);
                        writer.write(passportIdString + ",insert into account_info exception");
                        writer.write("\r\n");
                        writer.close();
                        continue;
                    }
                    if (isInsertSuccess) {
                        Account accountReturn;
                        try {
                            accountReturn = accountDAO.getAccountByPassportId(passportIdString);
                        } catch (Exception e) {
                            //查询account表异常
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\update_account_exception.txt", true);
                            writer.write(passportIdString + ",query account table exception");
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                        if (accountReturn == null) {
                            //查询account表中是否有此记录，理论上来讲，这些passportId在account表中都存在
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\update_account_failed.txt", true);
                            writer.write(passportIdString + ",account table is not this passportId");
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                        int row;
                        try {
                            //写account表的创建ip与创建时间
                            Account account = new Account();
                            account.setPassportId(passportIdString);
                            account.setRegIp(createIp);
                            account.setRegTime(date_createtime);
                            row = accountDAO.insertOrUpdateAccount(passportIdString, account);
                        } catch (Exception e) {
                            //插入到account表异常
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\update_account_exception.txt", true);
                            writer.write(passportIdString + ",insert into account table exception");
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                        if (row == 0) {
                            //插入到account表失败
                            FileWriter writer = new FileWriter("D:\\transfer\\account_info\\update_account_failed.txt", true);
                            writer.write(passportIdString + ",insert into account table error");
                            writer.write("\r\n");
                            writer.close();
                            continue;
                        }
                    } else {
                        //插入到account_info表失败
                        FileWriter writer = new FileWriter("D:\\transfer\\account_info\\insert_account_info_failed.txt", true);
                        writer.write(passportIdString + ",insert into account_info error");
                        writer.write("\r\n");
                        writer.close();
                        continue;
                    }
                } else {
                    //从sohu获取第三方用户信息失败
                    FileWriter writer = new FileWriter("D:\\transfer\\account_info\\get_from_sohu_failed.txt", true);
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
