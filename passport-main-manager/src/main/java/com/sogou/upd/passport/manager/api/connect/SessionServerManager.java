package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.model.app.AppConfig;

/**
 * User: mayan
 * Date: 13-12-4
 * Time: 下午8:43
 */
public interface SessionServerManager {
     public String createSession(AppConfig appConfig,String userId);
}
