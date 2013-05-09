package com.sogou.upd.passport.service.account.dataobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:58
 */
public class AccessTokenCipherDO {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenCipherDO.class);
    public static final String SEPARATOR = "|";

    public static final int ACCESS_PASSPORT_ID = 0;
    public static final int ACCESS_CLIENT_ID = 1;
    public static final int ACCESS_VAILD_TIME = 2;
    public static final int ACCESS_RANDOM = 3;
    public static final int ACCESS_INSTANCE_ID = 4;

    private String passportId;
    private int clientId;
    private long vaildTime;
    private String random;
    private String instanceId;

    public AccessTokenCipherDO() {
    }

    public AccessTokenCipherDO(String passportId, int clientId, long vaildTime, String random, String instanceId) {
        this.passportId = passportId;
        this.clientId = clientId;
        this.vaildTime = vaildTime;
        this.random = random;
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

    public long getVaildTime() {
        return vaildTime;
    }

    public void setVaildTime(long vaildTime) {
        this.vaildTime = vaildTime;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String structureEncryptString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.passportId).append(SEPARATOR);
        sb.append(this.clientId).append(SEPARATOR);
        sb.append(this.vaildTime).append(SEPARATOR);
        sb.append(this.random).append(SEPARATOR);
        sb.append(this.instanceId);
        return sb.toString();
    }

    public static AccessTokenCipherDO parseEncryptString(String decryTokenStr) throws Exception {
        String[] tokenArray = decryTokenStr.split("\\|");
        if (tokenArray.length != 5) {
            logger.error("AccessToken Decry String format error! str:{}", decryTokenStr);
            throw new IllegalArgumentException();
        }
        AccessTokenCipherDO accessTokenCipherDO = new AccessTokenCipherDO();
        accessTokenCipherDO.setPassportId(tokenArray[ACCESS_PASSPORT_ID]);
        accessTokenCipherDO.setClientId(Integer.parseInt(tokenArray[ACCESS_CLIENT_ID]));
        accessTokenCipherDO.setVaildTime(Long.parseLong(tokenArray[ACCESS_VAILD_TIME]));
        accessTokenCipherDO.setRandom(tokenArray[ACCESS_RANDOM]);
        accessTokenCipherDO.setInstanceId(tokenArray[ACCESS_INSTANCE_ID]);

        return accessTokenCipherDO;
    }
}
