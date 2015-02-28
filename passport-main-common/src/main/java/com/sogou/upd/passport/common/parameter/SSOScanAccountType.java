package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-28
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
public enum SSOScanAccountType {

    UNKNOWN(0, null, null),
    SOGOU(1, "sogou", "搜狗"),
    QQ(2, "qq", "QQ"),
    WEIBO(3, "weibo", "微博"),
    RENREN(4, "renren", "人人"),
    BAIDU(5, "baidu", "百度"),
    WEIXIN(6,"weixin","微信"),
    HUAWEI(7,"huawei","华为");

    private int value;
    private String enDescription;
    private String zhDescription;

    SSOScanAccountType(int value,String enDescription,String zhDescription){
        this.value=value;
        this.enDescription=enDescription;
        this.zhDescription=zhDescription;
    }

    public static String getSSOScanAccountType(String username){
        AccountTypeEnum accountType=AccountTypeEnum.getAccountType(username);

         if(accountType==null || accountType==AccountTypeEnum.UNKNOWN){
             return UNKNOWN.getZhDescription();
         }

        if(accountType==AccountTypeEnum.EMAIL|| accountType==AccountTypeEnum.PHONE||accountType==AccountTypeEnum.SOGOU
                ||accountType==AccountTypeEnum.SOHU)  {
            return SOGOU.getZhDescription();
        }

        if(accountType==AccountTypeEnum.QQ){
            return QQ.getZhDescription();
        }

        if(accountType==AccountTypeEnum.SINA){
            return WEIBO.getZhDescription();
        }

        if(accountType==AccountTypeEnum.RENREN){
            return RENREN.getZhDescription();
        }

        if(accountType==AccountTypeEnum.BAIDU){
            return BAIDU.getZhDescription();
        }

        if(accountType==AccountTypeEnum.WEIXIN){
            return WEIXIN.getZhDescription();
        }

        if(accountType==AccountTypeEnum.HUAWEI){
            return HUAWEI.getZhDescription();
        }

        return UNKNOWN.getZhDescription();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getEnDescription() {
        return enDescription;
    }

    public void setEnDescription(String enDescription) {
        this.enDescription = enDescription;
    }

    public String getZhDescription() {
        return zhDescription;
    }

    public void setZhDescription(String zhDescription) {
        this.zhDescription = zhDescription;
    }
}
