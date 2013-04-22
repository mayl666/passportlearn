package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.ReturnGeneratedKeys;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template use File | Settings |
 * File Templates.
 */
@DAO
public interface AccountDAO {

    /**
     * 根据passportId获取Account
     */
    @SQL("select * from account where passport_id=:passport_id")
    public Account getAccountByPassportId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 根据passportId删除用户的Account信息，
     * 内部调试接口使用
     */
    @SQL("delete from account where passport_id=:passport_id")
    public int deleteAccountByPassportId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改用户信息
     */
    @SQL("update account set passwd=:passwd where passport_id=:passport_id")
    public int modifyPassword(@SQLParam("passwd") String passwd, @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 验证合法，用户注册
     */
    @SQL(
            "insert into account(passport_id,passwd,mobile,reg_time,reg_ip,status,version,account_type) "
                    + "values (:passport_id,:a.passwd,:a.mobile,:a.regTime,:a.regIp,:a.status,:a.version,"
                    + ":a.accountType)")
    public int insertAccount(@SQLParam("passport_id") String passport_id, @SQLParam("a") Account account) throws DataAccessException;

}
