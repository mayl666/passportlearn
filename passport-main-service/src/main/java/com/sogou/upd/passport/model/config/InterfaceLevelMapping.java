package com.sogou.upd.passport.model.config;

import java.util.Date;

/**
 * 接口与等级映射类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-6
 * Time: 下午11:49
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceLevelMapping {

    private long id;                         //主键id
    private String interfaceName;            //接口名
    private String primaryLevel;             //初级,数值表示为0
    private String primaryLevelCount;        //初级对应的接口限制次数
    private String middleLevel;              //中级，数值表示为1
    private String middleLevelCount;         //中级对应的接口限制次数
    private String highLevel;                //高级，数值表示为2
    private String highLevelCount;           //高级对应的接口限制次数
    private Date createTime;                 //日期，新增记录是创建日期；修改记录是修改日期

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getPrimaryLevel() {
        return primaryLevel;
    }

    public void setPrimaryLevel(String primaryLevel) {
        this.primaryLevel = primaryLevel;
    }

    public String getPrimaryLevelCount() {
        return primaryLevelCount;
    }

    public void setPrimaryLevelCount(String primaryLevelCount) {
        this.primaryLevelCount = primaryLevelCount;
    }

    public String getMiddleLevel() {
        return middleLevel;
    }

    public void setMiddleLevel(String middleLevel) {
        this.middleLevel = middleLevel;
    }

    public String getMiddleLevelCount() {
        return middleLevelCount;
    }

    public void setMiddleLevelCount(String middleLevelCount) {
        this.middleLevelCount = middleLevelCount;
    }

    public String getHighLevel() {
        return highLevel;
    }

    public void setHighLevel(String highLevel) {
        this.highLevel = highLevel;
    }

    public String getHighLevelCount() {
        return highLevelCount;
    }

    public void setHighLevelCount(String highLevelCount) {
        this.highLevelCount = highLevelCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
