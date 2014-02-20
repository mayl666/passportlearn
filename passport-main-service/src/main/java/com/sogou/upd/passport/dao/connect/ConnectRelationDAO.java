package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.model.connect.ConnectRelation;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * connect_token的反查表，按openid分表
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午11:30
 */
@DAO
public interface ConnectRelationDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " connect_relation ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " openid, provider, passport_id, app_key ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :openid, :connectRelation.provider, :connectRelation.passportId, :connectRelation.appKey ";

    /**
     * 根据openid、provider、appkey获取唯一的用户
     *
     * @param openid
     * @param provider
     * @return
     * @throws DataAccessException
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where openid=:openid and provider=:provider and app_key=:app_key")
    public ConnectRelation getSpecifyConnectToken(@ShardBy @SQLParam("openid") String openid,
                                                  @SQLParam("provider") int provider, @SQLParam("app_key") String app_key)
            throws DataAccessException;

    /**
     * 根据openid和provider获取对应的用户
     *
     * @param openid
     * @param provider
     * @return
     * @throws DataAccessException
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where openid=:openid and provider=:provider")
    public List<ConnectRelation> listConnectRelation(@ShardBy @SQLParam("openid") String openid, @SQLParam("provider") int provider)
            throws DataAccessException;

    /**
     * 插入一条新记录
     */
    @SQL("insert into " +
            TABLE_NAME +
            "(" + ALL_FIELD + ") values(" + VALUE_FIELD + ")")
    public int insertConnectRelation(@ShardBy @SQLParam("openid") String openid, @SQLParam("connectRelation") ConnectRelation connectRelation)
            throws DataAccessException;

    /**
     * 删除一条记录
     * Unit Test使用
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where openid=:openid and provider=:provider")
    public int deleteConnectRelation(@ShardBy @SQLParam("openid") String openid, @SQLParam("provider") int provider) throws DataAccessException;
}
