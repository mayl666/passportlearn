package com.sogou.upd.passport.manager.app;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;

/**
 * 应用管理
 * User: mayan
 * Date: 13-4-16
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigureManager {
    /**
     * 验证clientId和clientSecret合法性
     * 如果不合法返回null
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AppConfig verifyClientVaild(int clientId, String clientSecret);

    public ConnectConfig obtainConnectConfig(int clientId, int provider);
   /**
   * 验证是否存在此应用
   * @param clientId
   * @return
   */
    public boolean checkAppIsExist(int clientId);

}
