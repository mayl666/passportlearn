package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountToken;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午10:46 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountTokenDAO {

    /**
     * 根据passportId获取AccountToken信息
     *
     * @param passport_id
     * @return
     */
    @SQL("select * from account_token where passport_id=:passport_id and client_id=:client_id and instance_id=:instance_id")
    public AccountToken getAccountTokenByPassportId(@SQLParam("passport_id") String passport_id, @SQLParam("client_id")
    int client_id, @SQLParam("instance_id") String instance_id) throws DataAccessException;

    /**
     * 根据passportId获取AccountToken信息
     *
     * @param passport_id
     * @return
     */
    @SQL("select * from account_token where passport_id=:passport_id")
    public List<AccountToken> listAccountTokenByPassportId(@SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 根据userid删除AccountToken信息，内部调试接口使用
     */
    @SQL("delete from account_token where passport_id=:passport_id")
    public int deleteAccountTokenByPassportId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 往用户状态表中插入一条记录
     */
    @SQL("insert into account_token(passport_id,access_token,refresh_token,access_valid_time,refresh_valid_time,"
            + "client_id,instance_id) values (:passport_id,:a.accessToken,:a.refreshToken,:a.accessValidTime,"
            + ":a.refreshValidTime,:a.clientId,:a.instanceId)")
    public int insertAccountToken(@SQLParam("passport_id") String passport_id, @SQLParam("a") AccountToken accountToken) throws
            DataAccessException;

    /**
     * 往用户状态表中插入一条记录
     * 有多个唯一索引时不要使用，存在则更新，不存在则插入
     */
    @SQL("insert into account_token(passport_id,access_token,refresh_token,access_valid_time,refresh_valid_time,client_id,"
            + "instance_id) values (:passport_id,:a.accessToken,:a.refreshToken,:a.accessValidTime,:a.refreshValidTime,"
            + ":a.clientId,:a.instanceId) "
            + "ON DUPLICATE KEY UPDATE "
            + "access_token=:a.accessToken,refresh_token=:a.refreshToken,access_valid_time=:a.accessValidTime,refresh_valid_time=:a.refreshValidTime")
    public int saveAccountToken(@SQLParam("passport_id") String passport_id, @SQLParam("a") AccountToken accountToken) throws
            DataAccessException;

    /**
     * 更新用户状态表
     */
    @SQL("update account_token set access_token=:a.accessToken,refresh_token=:a.refreshToken,access_valid_time=:a.accessValidTime,"
            + "refresh_valid_time=:a.refreshValidTime"
            + "where passport_id=:passport_id and client_id=:a.clientId and instance_id=:a.instanceId")
    public int updateAccountToken(@SQLParam("passport_id") String passport_id, @SQLParam("a") AccountToken accountToken) throws
            DataAccessException;

    /**
     * 根据passportId和clientId查询所有记录，返回list集合
     * 查除当前instanceId以外的列表也用该方法，因为sql不能出现‘<>’
     */
    @SQL("select * from account_token  where passport_id=:passport_id")
    public List<AccountToken> listAccountTokenByPassportIdAndClientId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 批量更新某用户对应的状态记录
     * 分表时候有问题，需要一条一条更新
     * TODO 需要修改
     */
    @SQL("update account_token set access_token=:a.accessToken,refresh_token=:a.refreshToken,"
            + "access_valid_time=:a.accessValidTime,refresh_valid_time=:a.refreshValidTime "
            + "where passport_id=:a.passportId and client_id=:a.clientId and instance_id=:a.instanceId")
    public int[] batchUpdateAccountToken(@SQLParam("a") List<AccountToken> updateAccountTokens) throws
            DataAccessException;

}
