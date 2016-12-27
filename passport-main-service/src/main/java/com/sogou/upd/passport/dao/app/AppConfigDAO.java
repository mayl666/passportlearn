package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.AppConfig;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午10:30 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AppConfigDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " app_config ";

    /**
     * 获取所有app_config
     */
    @SQL("select * from " + TABLE_NAME)
    public List<AppConfig> listAllAppConfig() throws DataAccessException;

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, client_id, sms_text, access_token_expiresin, refresh_token_expiresin, server_secret, client_secret, create_time, client_name, scope, server_ip ";

    @SQL("select max(client_id) from" + TABLE_NAME)
    public int getMaxClientId() throws DataAccessException;

    /**
     * 根据clientId获取AppConfig对象
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where client_id=:client_id")
    public AppConfig getAppConfigByClientId(@SQLParam("client_id") int client_id) throws DataAccessException;

    @SQL("insert into" +
            TABLE_NAME +
            "set client_id=:client_id, " +
            "sms_text=:sms_text, " +
            "access_token_expiresin=:access_token_expiresin, " +
            "refresh_token_expiresin=:refresh_token_expiresin, " +
            "server_secret=:server_secret, " +
            "client_secret=:client_secret, " +
            "client_name=:client_name")
    public int insertAppConfig(@SQLParam("client_id") int client_id,
                                   @SQLParam("sms_text") String sms_text,
                                   @SQLParam("access_token_expiresin") int access_token_expiresin,
                                   @SQLParam("refresh_token_expiresin") int refresh_token_expiresin,
                                   @SQLParam("server_secret") String server_secret,
                                   @SQLParam("client_secret") String client_secret,
                                   @SQLParam("client_name") String client_name) throws DataAccessException;

    @SQL("update" +
            TABLE_NAME +
            "set sms_text=:sms_text, " +
            "access_token_expiresin=:access_token_expiresin, " +
            "refresh_token_expiresin=:refresh_token_expiresin, " +
            "client_name=:client_name " +
            "where client_id=:client_id")
    public int updateAppConfig(@SQLParam("client_id") int client_id,
                                   @SQLParam("sms_text") String sms_text,
                                   @SQLParam("access_token_expiresin") int access_token_expiresin,
                                   @SQLParam("refresh_token_expiresin") int refresh_token_expiresin,
                                   @SQLParam("client_name") String client_name) throws DataAccessException;

    @SQL("update" +
            TABLE_NAME +
            "set client_name=:client_name " +
            "where client_id=:client_id")
    public int updateAppConfigName(@SQLParam("client_id") int client_id,
                                   @SQLParam("client_name") String client_name) throws DataAccessException;

    @SQL("delete from" + TABLE_NAME  + "where client_id=:client_id")
    public int deleteAppConfig(@SQLParam("client_id") int client_id) throws DataAccessException;
}
