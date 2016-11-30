package com.sogou.upd.passport.common.model.useroperationlog;

import org.apache.commons.collections.map.LinkedMap;

import java.util.Map;

/**
 * 用于记录用户行为的对象
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-20
 * Time: 下午2:04
 */
public class UserOperationLog {

    //操作者
    private String passportId;

    //操作app
    private String clientId;

    // 操作IP
    private String ip;

    //返回码
    private String resultCode;

    //用户执行的操作
    private String userOperation;

    //其它信息
    private Map<String,String> otherMessageMap;

    private String udid;

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    /**
     * 用户操作行为记录
     * @param passportId
     * @param clientId
     * @param resultCode
     * @param ip
     */
   public UserOperationLog(String passportId,String clientId,String resultCode, String ip){
       this.passportId=passportId;
       this.clientId=clientId;
       this.ip = ip;
       this.resultCode=resultCode;
       otherMessageMap=new LinkedMap();
   }

    /**
     * 构造用户行为记录的log
     * @param passportId  用户id
     * @param userOperation 用户执行的操作
     * @param clientId    发起操作的appId
     * @param resultCode  操作结果，具体参见{@link com.sogou.upd.passport.common.utils.ErrorUtil}
     */
    public UserOperationLog(String passportId,String userOperation,String clientId,String resultCode, String ip){
        this.passportId=passportId;
        this.userOperation=userOperation;
        this.clientId=clientId;
        this.ip = ip;
        this.resultCode=resultCode;
        otherMessageMap=new LinkedMap();
    }

    /**
     * 构造用户行为记录的log
     * @param passportId  用户id
     * @param userOperation 用户执行的操作
     * @param clientId    发起操作的appId
     * @param resultCode  操作结果，具体参见{@link com.sogou.upd.passport.common.utils.ErrorUtil}
     */
    public UserOperationLog(String passportId,String userOperation,String clientId,String resultCode, String ip,String udid){
        this.passportId=passportId;
        this.userOperation=userOperation;
        this.clientId=clientId;
        this.ip = ip;
        this.resultCode=resultCode;
        otherMessageMap=new LinkedMap();
        this.udid = udid;
    }

    public void putOtherMessage(String key,String value){
        otherMessageMap.put(key,value);
    }

    public Map<String, String> getOtherMessageMap() {
        return otherMessageMap;
    }

    public void setOtherMessageMap(Map<String, String> otherMessageMap) {
        this.otherMessageMap = otherMessageMap;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getUserOperation() {
        return userOperation;
    }

    public void setUserOperation(String userOperation) {
        this.userOperation = userOperation;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



}
