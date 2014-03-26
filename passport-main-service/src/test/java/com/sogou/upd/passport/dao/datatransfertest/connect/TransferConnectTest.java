package com.sogou.upd.passport.dao.datatransfertest.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.dao.connect.OpenTokenInfo;
import com.sogou.upd.passport.dao.connect.OpenTokenInfoDAO;
import com.sogou.upd.passport.model.connect.ConnectToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-2-24
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public class TransferConnectTest extends BaseTest {

    @Autowired
    private OpenTokenInfoDAO openTokenInfoDAO;

    @Autowired
    private ConnectTokenDAO connectTokenDAO;

    @Test
    public void modifyUpdateTime() {
        long start = System.currentTimeMillis();
        try {
            List<String> dataList = FileIOUtil.readFileByLines("d:\\connect_token_updatetime_test.csv");
            BufferedWriter bw = FileIOUtil.newWriter("d:\\connect_token_updatetime_error.txt");
            for (String data : dataList) {
                String[] array = data.split(",");
                if (array.length != 3) {
                    bw.write("data length error: " + data + "\n");
                    bw.flush();
                } else {
                    int provider = Integer.parseInt(array[0]);
                    String appKey = array[1];
                    String openId = array[2];
                    String providerStr = AccountTypeEnum.getProviderStr(provider);
                    String passportId = AccountTypeEnum.generateThirdPassportId(openId, providerStr);
                    OpenTokenInfo openTokenInfo = openTokenInfoDAO.getOpenTokenInfo(openId, providerStr, appKey);
                    if (openTokenInfo != null) {
                        Date updateTime = openTokenInfo.getCreatetime();
                        ConnectToken connectToken = new ConnectToken();
                        connectToken.setPassportId(passportId);
                        connectToken.setOpenid(openId);
                        connectToken.setAppKey(appKey);
                        connectToken.setProvider(provider);
                        connectToken.setUpdateTime(updateTime);
                        int row = connectTokenDAO.updateConnectToken(passportId, connectToken);
                        if (row != 1) {
                            bw.write("updateConnectToken is fail, data:" + data + "\n");
                            bw.flush();
                        }
                    } else {
                        bw.write("getOpenTokenInfo is null, data:" + data + "\n");
                        bw.flush();
                    }
                }
            }
            long end = System.currentTimeMillis();
            bw.write("use time:" + (end - start) / 60000 + "m");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("use time:" + (end - start) / 60000 + "m");
        }
    }

}
