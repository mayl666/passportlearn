package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.ConnectConfig;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

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

    /**
     * 值列表
     */
    String VALUE_FIELD = " :connectConfig.id, :connectConfig.clientId, :connectConfig.provider, :connectConfig.appKey, :connectConfig.appSecret, :connectConfig.scope, :connectConfig.createTime ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " client_id = :connectConfig.clientId, provider = :connectConfig.provider, app_key = :connectConfig.appKey, app_secret = :connectConfig.appSecret, scope = :connectConfig.scope ";


    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where client_id=:client_id and provider=:provider")
    public ConnectConfig getConnectConfigByClientIdAndProvider(@SQLParam("client_id") int client_id, @SQLParam("provider") int provider) throws
            DataAccessException;

    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where provider=:provider and app_key=:app_key")
    public ConnectConfig getConnectConfigByAppIdAndProvider(@SQLParam("app_key") String appId, @SQLParam("provider") int provider) throws
            DataAccessException;

    /**
     * 更新用户状态表
     */
    @SQL("update " +
            TABLE_NAME +
            " set " +
            UPDATE_FIELD
            + "where client_id=:connectConfig.clientId and provider=:connectConfig.provider")
    public int updateConnectConfig(@SQLParam("connectConfig") ConnectConfig connectConfig) throws DataAccessException;
}
