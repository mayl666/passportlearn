package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.parameter.CommonParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:58
 * To change this template use File | Settings | File Templates.
 */
public class RefreshTokenCipherDO {

    private static Logger logger = LoggerFactory.getLogger(RefreshTokenCipherDO.class);

    public static final int REFRESH_PASSPORT_ID = 0;
    public static final int REFRESH_CLIENT_ID = 1;
    public static final int REFRESH_TIMESTAMP = 2;
    public static final int REFRESH_INSTANCE_ID = 3;

    private String passportId;
    private int clientId;
    private long timeStamp;
    private String instanceId;

    public RefreshTokenCipherDO() {
    }

    public RefreshTokenCipherDO(String passportId, int clientId, long timeStamp, String instanceId) {
        this.passportId = passportId;
        this.clientId = clientId;
        this.timeStamp = timeStamp;
        this.instanceId = instanceId;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String structureEncryptString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.passportId).append(CommonParameters.SEPARATOR_1);
        sb.append(this.clientId).append(CommonParameters.SEPARATOR_1);
        sb.append(this.timeStamp).append(CommonParameters.SEPARATOR_1);
        sb.append(this.instanceId);
        return sb.toString();
    }

    public static RefreshTokenCipherDO parseEncryptString(String decryTokenStr) throws Exception {
        String[] tokenArray = decryTokenStr.split("\\|");
        if (tokenArray.length != 4) {
            logger.error("AccessToken Decry String format error! str:{}", decryTokenStr);
            throw new IllegalArgumentException();
        }
        RefreshTokenCipherDO refreshTokenCipherDO = new RefreshTokenCipherDO();
        refreshTokenCipherDO.setPassportId(tokenArray[REFRESH_PASSPORT_ID]);
        refreshTokenCipherDO.setClientId(Integer.parseInt(tokenArray[REFRESH_CLIENT_ID]));
        refreshTokenCipherDO.setTimeStamp(Long.parseLong(tokenArray[REFRESH_TIMESTAMP]));
        refreshTokenCipherDO.setInstanceId(tokenArray[REFRESH_INSTANCE_ID]);

        return refreshTokenCipherDO;
    }
}
