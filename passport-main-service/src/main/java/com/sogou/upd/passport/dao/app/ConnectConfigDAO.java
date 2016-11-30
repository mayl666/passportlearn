package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.ConnectConfig;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午1:10
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface ConnectConfigDAO {
    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " connect_config ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, client_id, provider, app_key, app_secret, scope, create_time ";

    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where client_id=:client_id and provider=:provider")
    public ConnectConfig getConnectConfigByClientIdAndProvider(@SQLParam("client_id") int client_id, @SQLParam("provider") int provider) throws
            DataAccessException;

    @SQL("select" +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where client_id=:client_id")
    public List<ConnectConfig> listConnectConfigByClientId(@SQLParam("client_id") int client_id) throws
            DataAccessException;

    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where provider=:provider and app_key=:app_key")
    public ConnectConfig getConnectConfigByAppIdAndProvider(@SQLParam("app_key") String appId, @SQLParam("provider") int provider) throws
            DataAccessException;

    @SQL("insert into" +
            TABLE_NAME +
            "set client_id=:client_id, " +
            "provider=:provider, " +
            "app_key=:app_key, " +
            "app_secret=:app_secret, " +
            "scope=:scope")
    public int insertConnectConfig(@SQLParam("client_id") int client_id,
                                   @SQLParam("provider") int provider,
                                   @SQLParam("app_key") String app_key,
                                   @SQLParam("app_secret") String app_secret,
                                   @SQLParam("scope") String scope) throws DataAccessException;

    /**
     * 更新用户状态表
     */
    @SQL("update" +
            TABLE_NAME +
            "set scope=:scope " +
            "where client_id=:client_id " +
            "and provider=:provider " +
            "and app_key=:app_key")
    public int updateConnectConfig(@SQLParam("client_id") int client_id,
                                   @SQLParam("provider") int provider,
                                   @SQLParam("app_key") String app_key,
                                   @SQLParam("scope") String scope) throws DataAccessException;

    /**
     * 删除配置项
     */
    @SQL("delete from" + TABLE_NAME  +
            "where client_id=:client_id " +
            "and provider=:provider " +
            "and app_key=:app_key")
    public int deleteConnectConfig(@SQLParam("client_id") int client_id,
                                   @SQLParam("provider") int provider,
                                   @SQLParam("app_key") String app_key) throws DataAccessException;

}
