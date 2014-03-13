package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-25
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class AddConnectUserInfoThread implements Runnable {

    private CountDownLatch latch;
    private String fileName;
    private ConnectAuthService connectAuthService;
    private ConnectTokenService connectTokenService;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public AddConnectUserInfoThread(CountDownLatch latch, String fileName, ConnectTokenService connectTokenService, ConnectAuthService connectAuthService) {
        this.latch = latch;
        this.fileName = fileName;
        this.connectTokenService = connectTokenService;
        this.connectAuthService = connectAuthService;
    }

    @Override
    public void run() {
        int count = 0;
        BufferedReader reader = null;
        String logOpenId = null;
        try {
            long start = System.currentTimeMillis();
            File file = new File(this.fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String[] rowString = tempString.split(",");
                String passportId = rowString[1]; //passportId;
                logOpenId = passportId;
                int provider = Integer.parseInt(rowString[2]);//provider
                String appKey = rowString[3];  //appKey
                String openId = rowString[4];//openId
                String accessToken = rowString[5];//accessToken
                String expiredInString = rowString[6];//accessToken的有效期
                long expiredIn = Long.parseLong(expiredInString);
                String refreshToken = rowString[7];//refreshToken
                String dateString = rowString[13].replace("/", "-");//数据库中该passport记录对应的修改时间串
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(dateString);
                //1.调用第三方API获取第三方用户信息
                OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
                ConnectConfig connectConfig = new ConnectConfig();
                connectConfig.setAppKey(appKey);
                ConnectUserInfoVO connectUserInfoVO;
                try {
                    connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
                } catch (Exception e) {
                    //1.1获取第三方用户信息失败，记录格式为passportId,provider,appKey,openId,accessToken,date
                    FileWriter writer = new FileWriter("/search/passport/log/liuling/obtain_exception.txt", true);
                    writer.write(passportId + "," + provider + "," + appKey + "," + openId + "," + accessToken + "," + rowString[13] + ",error:obtain userinfo exception!" + e.getMessage());
                    writer.write("\r\n");
                    writer.close();
                    count++;
                    continue;
                }
                if (connectUserInfoVO == null) {
                    //1.2第三方API调用失败，记录passportId
                    FileWriter writer = new FileWriter("/search/passport/log/liuling/obtain_exception.txt", true);
                    writer.write(passportId + "," + provider + "," + appKey + "," + openId + "," + accessToken + "," + rowString[13] + ",error:user info is null");
                    writer.write("\r\n");
                    writer.close();
                    count++;
                    continue;
                } else {
                    //暂时先不更新缓存
//                connectAuthService.initialOrUpdateConnectUserInfo(passportId, connectUserInfoVO);
                    //2.获取第三方头像、昵称信息
                    ConnectToken connectToken = new ConnectToken();
                    connectToken.setConnectUniqname(StringUtil.filterConnectUniqname(connectUserInfoVO.getNickname())); //第三方用户昵称
                    connectToken.setAvatarSmall(connectUserInfoVO.getAvatarSmall());  //第三方用户小头像
                    connectToken.setAvatarMiddle(connectUserInfoVO.getAvatarMiddle()); //第三方用户中头像
                    connectToken.setAvatarLarge(connectUserInfoVO.getAvatarLarge());  //第三方用户大头像
                    connectToken.setPassportId(passportId);
                    connectToken.setAppKey(appKey);
                    connectToken.setProvider(provider);
                    connectToken.setOpenid(openId);
                    connectToken.setAccessToken(accessToken);
                    connectToken.setExpiresIn(expiredIn);
                    connectToken.setRefreshToken(refreshToken);
                    connectToken.setGender(String.valueOf(connectUserInfoVO.getGender()));//第三方用户性别
                    connectToken.setUpdateTime(date); //connectToken表的更新时间还保持原来表中的时间
                    boolean isUpdateSuccess;
                    try {
                        isUpdateSuccess = connectTokenService.insertOrUpdateConnectToken(connectToken);
                    } catch (Exception e) {
                        //2.1更新connect_token表异常，记录passportId
                        FileWriter writer = new FileWriter("/search/passport/log/liuling/update_error.txt", true);
                        writer.write(passportId + "," + provider + "," + appKey + "," + openId + "," + accessToken + "," + rowString[13] + ",error:update exception!" + e.getMessage());
                        writer.write("\r\n");
                        writer.close();
                        count++;
                        continue;
                    }
                    if (!isUpdateSuccess) {
                        //2.2更新sogou connect_token表失败的记录下来,格式为passportId,provider,appKey,openId,accessToken,date
                        FileWriter writer = new FileWriter("/search/passport/log/liuling/update_error.txt", true);
                        writer.write(passportId + "," + provider + "," + appKey + "," + openId + "," + accessToken + "," + rowString[13] + ",error:update failed");
                        writer.write("\r\n");
                        writer.close();
                        count++;
                        continue;
                    }
                }
                count++;
            }
            reader.close();
            System.out.println(Thread.currentThread().getName() + ":" + count);
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
