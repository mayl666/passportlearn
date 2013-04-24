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
     * 更新AccountConnect
     */
    @SQL("update connect_token set "
            + "#if(:a.refreshToken != null){refresh_token=:a.refreshToken,} "
            + "#if(:a.expiresIn > 0){expires_in=:a.expiresIn,}"
            + "#if(:a.accessToken != null){access_token=:a.accessToken} "
            + "where passport_id=:passport_id and openid=:a.openid and provider=:a.provider and app_key=:a.appKey")
    public int updateConnectToken(@SQLParam("passport_id") String passport_id, @SQLParam("a") ConnectToken connectToken)
            throws DataAccessException;

    /**
     * 插入一条新记录
     */
    @SQL("insert into connect_token(passport_id,app_key,provider,openid,access_token,"
            + "expires_in,refresh_token,create_time) values(:passport_id,:a.appKey,"
            + ":a.provider,:a.openid,:a.accessToken,:a.expiresIn,:a.refreshToken,:a.createTime)")
    public int insertAccountConnect(@SQLParam("passport_id") String passport_id, @SQLParam("a") ConnectToken accountConnect)
            throws DataAccessException;

    /**
     * 删除一条记录，（Unit Test使用）
     */
    @SQL("delete from connect_token where passport_id=:passport_id")
    public int deleteConnectTokenByPassportId(@SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 根据passportId获取openId
     * 因为缓存里存的是passportId：ConnectToken，这里不返回String，返回ConnectToken
     */
    @SQL("select * from connect_token where "
            + "(passport_id=:passport_id and provider=:provider and app_key=:app_key)")
    public ConnectToken getSpecifyConnectToken(@SQLParam("passport_id") String passport_id, @SQLParam("provider") int provider,
                                               @SQLParam("app_key") String app_key) throws DataAccessException;


}
