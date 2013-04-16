package com.sogou.upd.passport.manager.app;

/**
 * 应用管理
 * User: mayan
 * Date: 13-4-16
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public interface AppConfigManager {
    /**
     * 验证client合法性
     * @param clientId
     * @param clientSecret
     * @return
     */
    public boolean verifyClientVaild(int clientId, String clientSecret);
}
