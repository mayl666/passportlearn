package com.sogou.upd.passport.service.account.dataobject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-25
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
public class WapActiveEmailDao extends BaseActiveEmailDao {

    /**
     * wap页面的皮肤值,red-红色；默认green，绿色
     */
    private String skin;

    /**
     * wap版本:1-简易版；2-炫彩版；5-触屏版  0-返回json数据。此接口值为5。
     */
    private String v;

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
