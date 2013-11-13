package com.sogou.upd.passport.model.config;

/**
 * 应用与等级映射类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-7
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */
public class ClientIdLevelMapping {

    private long id;                  //主键id
    private String clientId;          //应用id
    private String levelInfo;         //等级信息
    private String interfaceName;     //接口名称，todo 为以后单独指定某一应用下某一接口等级所做的扩展，暂时没用到此参数

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLevelInfo() {
        return levelInfo;
    }

    public void setLevelInfo(String levelInfo) {
        this.levelInfo = levelInfo;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
