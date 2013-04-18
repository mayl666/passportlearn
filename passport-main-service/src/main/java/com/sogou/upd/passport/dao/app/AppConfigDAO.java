package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.AppConfig;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午10:30 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AppConfigDAO {

  /**
   * 根据clientId获取AppConfig对象
   */
  @SQL("select * from app_config where client_id=:client_id")
  public AppConfig getAppConfigByClientId(@SQLParam("client_id") int client_id);

}
