package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.model.connect.ConnectToken;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * connect_token表的DAO操作
 * User: shipengzhi
 * Date: 13-4-18 Time:
 * 下午3:34
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
    String ALL_FIELD = " passport_id, provider, app_key, openid, access_token, expires_in, refresh_token, " +
            "connect_uniqname, avatar_small, avatar_middle, avatar_large, gender, update_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :passport_id, :connectToken.provider, :connectToken.appKey, :connectToken.openid, " +
            ":connectToken.accessToken, :connectToken.expiresIn, :connectToken.refreshToken, " +
            ":connectToken.connectUniqname, :connectToken.avatarSmall, :connectToken.avatarMiddle, :connectToken.avatarLarge," +
            " :connectToken.gender, :connectToken.updateTime ";

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
    public ConnectToken getSpecifyConnectToken(@ShardBy @SQLParam("passport_id") String passport_id, @SQLParam("provider") int provider,
                                               @SQLParam("app_key") String app_key) throws DataAccessException;

    /**
     * 根据passportId、provider取ConnectToken列表
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where "
            + "(passport_id=:passport_id and provider=:provider)")
    public List<ConnectToken> getConnectTokenList(@ShardBy @SQLParam("passport_id") String passport_id, @SQLParam("provider") int provider)
            throws DataAccessException;


    /**
     * 更新AccountConnect
     */
    @SQL("update " +
            TABLE_NAME +
            " set "
            + "#if(:connectToken.accessToken != null){access_token=:connectToken.accessToken,} "
            + "#if(:connectToken.expiresIn > 0){expires_in=:connectToken.expiresIn,}"
            + "#if(:connectToken.refreshToken != null){refresh_token=:connectToken.refreshToken,} "
            + "#if(:connectToken.connectUniqname != null){connect_uniqname=:connectToken.connectUniqname,} "
            + "#if(:connectToken.avatarSmall != null){avatar_small=:connectToken.avatarSmall,} "
            + "#if(:connectToken.avatarMiddle != null){avatar_middle=:connectToken.avatarMiddle,} "
            + "#if(:connectToken.avatarLarge != null){avatar_large=:connectToken.avatarLarge,} "
            + "#if(:connectToken.gender != null){gender=:connectToken.gender,} "
            + "#if(:connectToken.updateTime != null){update_time=:connectToken.updateTime} "
            + "where passport_id=:passport_id and provider=:connectToken.provider and app_key=:connectToken.appKey")
    public int updateConnectToken(@ShardBy @SQLParam("passport_id") String passport_id, @SQLParam("connectToken") ConnectToken connectToken)
            throws DataAccessException;

    /**
     * 插入一条新记录
     */
    @SQL("insert into " +
            TABLE_NAME +
            "(" + ALL_FIELD + ") values(" + VALUE_FIELD + ")")
    public int insertAccountConnect(@ShardBy @SQLParam("passport_id") String passport_id, @SQLParam("connectToken") ConnectToken connectToken)
            throws DataAccessException;


    /**
     * 插入或修改一条新记录
     */
    @SQL("insert into " +
            TABLE_NAME +
            "(" + ALL_FIELD + ") values (" + VALUE_FIELD + ") on duplicate key "
            + "update "
            + "#if(:connectToken.accessToken != null){access_token=:connectToken.accessToken,} "
            + "#if(:connectToken.expiresIn > 0){expires_in=:connectToken.expiresIn,} "
            + "#if(:connectToken.refreshToken != null){refresh_token=:connectToken.refreshToken,} "
            + "#if(:connectToken.connectUniqname != null){connect_uniqname=:connectToken.connectUniqname,} "
            + "#if(:connectToken.avatarSmall != null){avatar_small=:connectToken.avatarSmall,} "
            + "#if(:connectToken.avatarMiddle != null){avatar_middle=:connectToken.avatarMiddle,} "
            + "#if(:connectToken.avatarLarge != null){avatar_large=:connectToken.avatarLarge,} "
            + "#if(:connectToken.gender != null){gender=:connectToken.gender,} "
            + "#if(:connectToken.updateTime != null){update_time=:connectToken.updateTime}")
    public int insertOrUpdateAccountConnect(@ShardBy @SQLParam("passport_id") String passport_id, @SQLParam("connectToken") ConnectToken connectToken)
            throws DataAccessException;

    /**
     * 删除一条记录，（Unit Test使用）
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public int deleteConnectTokenByPassportId(@ShardBy @SQLParam("passport_id") String passport_id) throws DataAccessException;
}
