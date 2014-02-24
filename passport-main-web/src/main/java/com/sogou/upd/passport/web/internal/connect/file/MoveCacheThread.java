package com.sogou.upd.passport.web.internal.connect.file;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.ConnectTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-23
 * Time: 下午7:08
 * To change this template use File | Settings | File Templates.
 */
public class MoveCacheThread implements Runnable {
    private CountDownLatch latch;
    private String fileName;
    private ConnectTokenService connectTokenService;
    private ConnectApiManager proxyConnectApiManager;
    private static final Logger logger = LoggerFactory.getLogger(MoveCacheThread.class);

    public MoveCacheThread(CountDownLatch latch, String fileName, ConnectTokenService connectTokenService, ConnectApiManager proxyConnectApiManager) {
        this.latch = latch;
        this.fileName = fileName;
        this.connectTokenService = connectTokenService;
        this.proxyConnectApiManager = proxyConnectApiManager;
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
                int provider = Integer.parseInt(rowString[3]);   //provider
                String passportIdString = AccountTypeEnum.generateThirdPassportId(openIdString, rowString[3]);
                String sohuFileToken = rowString[1];   //accessToken
                String appKey = ConnectTypeEnum.getAppKey(provider); //根据provider获取appKey
                ConnectToken connectToken = connectTokenService.queryConnectToken(passportIdString, provider, appKey);
                if (connectToken == null) {
                    //1.sohu导出的文件中有，但Sogou库中没有，需要记录下来,格式为：openid,sohuFileToken
                    FileWriter writer = new FileWriter("D:\\sogou_not_exist.txt", true);
                    writer.write(openIdString + "," + sohuFileToken);
                    writer.write("\r\n");
                    writer.close();
                } else {
                    String sogouDBToken = connectToken.getAccessToken();
                    BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
                    baseOpenApiParams.setOpenid(passportIdString);
                    baseOpenApiParams.setUserid(passportIdString);
                    Result result = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
                    if (result.isSuccess()) {
                        HashMap<String, String> map = (HashMap<String, String>) result.getModels().get("result");
                        String sohuOnLineToken = map.get("access_token").toString();
                        if (!sohuOnLineToken.equals(sogouDBToken)) {
                            //2.sohu导出文件中存在的token与从sogou库中查出的token不一样，需要记录下来，格式为：passportId，sohuFileToken，sohuOnLineToken,sogouDBToken
                            FileWriter writer = new FileWriter("D:\\sogou_sohu_different.txt", true);
                            writer.write(passportIdString + "," + sohuFileToken + "," + sohuOnLineToken + "," + sogouDBToken);
                            writer.write("\r\n");
                            writer.close();
                        }
                    } else {
                        String code = result.getCode();
                        if (code.equals(ErrorUtil.CONNECT_TOKEN_INVALID)) {
                            //3.从sogou库中查询token已经过期了，需要记录下来，格式为：passportId,sogouToken
                            FileWriter writer = new FileWriter("D:\\sohu_expired.txt", true);
                            writer.write(passportIdString + "," + sohuFileToken + "," + sogouDBToken);
                            writer.write("\r\n");
                            writer.close();
                        } else {
                            //4.其它原因导致没有查询成功的token，需要记录下来，格式为：passportId,sogouToken
                            FileWriter writer = new FileWriter("D:\\sohu_other.txt", true);
                            writer.write(passportIdString + "," + result.toString());
                            writer.write("\r\n");
                            writer.close();
                        }
                    }
                }
            }
            reader.close();
            System.out.println(Thread.currentThread().getName() + ":" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error("出错记录openid为：" + logOpenId, e);
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

