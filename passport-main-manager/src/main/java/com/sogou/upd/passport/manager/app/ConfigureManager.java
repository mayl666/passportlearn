package com.sogou.upd.passport.manager.app;

import com.sogou.upd.passport.common.result.Result;
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
     * 验证client合法性
     * @param clientId
     * @param clientSecret
     * @return
     */
    public boolean verifyClientVaild(int clientId, String clientSecret);

    public ConnectConfig obtainConnectConfig(int clientId, int provider);
   /**
   * 验证是否存在此应用
   * @param clientId
   * @return
   */
    public boolean checkAppIsExist(int clientId);

    /**
     * 校验接口传入的code是否正确
     * 校验时间戳是否在有效期内
     * @param uid 为passportId或者mobile
     * @param clientId
     * @param ct
     * @param originalCode
     * @return
     */
    public Result verifyInternalRequest(String uid, int clientId, long ct, String originalCode);
}
