package com.sogou.upd.passport.dao.app;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-27
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */

/**
 *
 */

import com.sogou.upd.passport.model.app.AppConfig;

/**
 * 配置表接口
 */
public interface AppConfigMapper {

    /**
     * 根据clientId获取AppConfig对象
     * @param clientId
     * @return
     */
    public AppConfig getAppConfigByClientId(int clientId);
}
