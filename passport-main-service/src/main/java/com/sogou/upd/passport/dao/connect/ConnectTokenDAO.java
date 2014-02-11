package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.model.connect.ConnectToken;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-18 Time: 下午3:34 To change this template use File | Settings |
 * File Templates.
 */
@DAO
public interface ConnectTokenDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " connect_token ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, passport_id, app_key, provider, openid, access_token, expires_in, refresh_token, create_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :connectToken.id, :connectToken.passportId, :connectToken.appKey, :connectToken.provider, :connectToken.openid, :connectToken.accessToken, :connectToken.expiresIn, :connectToken.refreshToken, :connectToken.createTime ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " passport_id = :connectToken.passportId, app_key = :connectToken.appKey, provider = :connectToken.provider, openid = :connectToken.openid, access_token = :connectToken.accessToken, expires_in = :connectToken.expiresIn, refresh_token = :connectToken.refreshToken, create_time = :connectToken.createTime ";

    /**
     * 更新AccountConnect
     */
    @SQL("update " +
            TABLE_NAME +
            " set "
            + "#if(:accountConnect.refreshToken != null){refresh_token=:accountConnect.refreshToken,} "
            + "#if(:accountConnect.expiresIn > 0){expires_in=:accountConnect.expiresIn,}"
            + "#if(:accountConnect.accessToken != null){access_token=:accountConnect.accessToken} "
            + "#if(:accountConnect.createTime != null){create_time=:accountConnect.createTime} "
            + "where passport_id=:passport_id and openid=:accountConnect.openid and provider=:accountConnect.provider and app_key=:accountConnect.appKey")
    public int updateConnectToken(@SQLParam("passport_id") String passport_id, @SQLParam("accountConnect") ConnectToken connectToken)
            throws DataAccessException;

    /**
     * 插入一条新记录
     */
    @SQL("insert into " +
            TABLE_NAME +
            "(passport_id,app_key,provider,openid,access_token,"
            + "expires_in,refresh_token,create_time) values(:passport_id,:accountConnect.appKey,"
            + ":accountConnect.provider,:accountConnect.openid,:accountConnect.accessToken,:accountConnect.expiresIn,:accountConnect.refreshToken,:accountConnect.createTime)")
    public int insertAccountConnect(@SQLParam("passport_id") String passport_id, @SQLParam("accountConnect") ConnectToken accountConnect)
            throws DataAccessException;

    /**
     * 删除一条记录，（Unit Test使用）
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public int deleteConnectTokenByPassportId(@SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 根据passportId获取openId
     * 因为缓存里存的是passportId：ConnectToken，这里不返回String，返回ConnectToken
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where "
            + "(passport_id=:passport_id and provider=:provider and app_key=:app_key)")
    public ConnectToken getSpecifyConnectToken(@SQLParam("passport_id") String passport_id, @SQLParam("provider") int provider,
                                               @SQLParam("app_key") String app_key) throws DataAccessException;


}
