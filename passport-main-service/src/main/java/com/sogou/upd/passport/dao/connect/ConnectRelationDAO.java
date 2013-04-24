package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * connectToken的反查表，按openid分表
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface ConnectRelationDAO {

    /**
     * 根据openid和provider获取对应的用户
     *
     * @param openid
     * @param provider
     * @return
     * @throws DataAccessException
     */
    @SQL("select * from connect_relation where openid=:openid and provider=:provider")
    public List<ConnectRelation> listConnectRelation(@SQLParam("openid") String openid, @SQLParam("provider") int provider)
            throws DataAccessException;

    /**
     * 插入一条新记录
     */
    @SQL("insert into connect_relation(openid,provider,passport_id,app_key) values(:openid,:a.provider,:a.passportId,:a.appKey)")
    public int insertConnectRelation(@SQLParam("openid") String openid, @SQLParam("a") ConnectRelation connectRelation)
            throws DataAccessException;

    /**
     * 删除一条记录
     * Unit Test使用
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from connect_relation where openid=:openid and provider=:provider")
    public int deleteConnectRelation(@SQLParam("openid") String openid, @SQLParam("provider") int provider) throws DataAccessException;
}
