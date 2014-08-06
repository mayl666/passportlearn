package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * wap端构造激活邮件参数对象
 * User: shipengzhi
 * Date: 14-7-26
 * Time: 下午6:45
 * To change this template use File | Settings | File Templates.
 */
public class WapActiveEmailDO extends ActiveEmailDO {

    private String skin; //wap页面的皮肤值,red-红色；默认green，绿色

    private String v; //wap版本:1-简易版；2-炫彩版；5-触屏版  0-返回json数据。

    public WapActiveEmailDO(String passportId, int clientId, String ru, AccountModuleEnum module, String toEmail, boolean saveEmail, String skin, String v) {
        super(passportId, clientId, ru, module, toEmail, saveEmail);
        this.skin = skin;
        this.v = v;
    }

    public String getPrefix() {
        return  CommonConstant.DEFAULT_WAP_INDEX_URL;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
