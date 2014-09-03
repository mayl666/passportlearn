package com.sogou.upd.passport.manager.app;

/**
 * 应用管理
 * User: mayan
 * Date: 13-4-16
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigureManager {
   /**
   * 验证是否存在此应用
   * @param clientId
   * @return
   */
    public boolean checkAppIsExist(int clientId);

}
