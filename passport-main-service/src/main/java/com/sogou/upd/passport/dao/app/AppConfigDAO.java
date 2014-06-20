package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.AppConfig;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

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
     * 所有字段列表
     */
    String ALL_FIELD = " id, client_id, sms_text, access_token_expiresin, refresh_token_expiresin, server_secret, client_secret, create_time, client_name, scope, server_ip ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :appConfig.id, :appConfig.clientId, :appConfig.smsText, :appConfig.accessTokenExpiresin, :appConfig.refreshTokenExpiresin, :appConfig.serverSecret, :appConfig.clientSecret, :appConfig.createTime, :appConfig.clientName ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " client_id = :appConfig.clientId, sms_text = :appConfig.smsText, access_token_expiresin = :appConfig.accessTokenExpiresin, refresh_token_expiresin = :appConfig.refreshTokenExpiresin, server_secret = :appConfig.serverSecret, client_secret = :appConfig.clientSecret, create_time = :appConfig.createTime, client_name = :appConfig.clientName ";

    /**
     * 根据clientId获取AppConfig对象
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where client_id=:client_id")
    public AppConfig getAppConfigByClientId(@SQLParam("client_id") int client_id) throws DataAccessException;

    /*----- !!!注意，如果需要更新client_name，则需要同步更新AppConfigServiceImpl中的CLIENTNAMES_MAP!!!-----*/
}
