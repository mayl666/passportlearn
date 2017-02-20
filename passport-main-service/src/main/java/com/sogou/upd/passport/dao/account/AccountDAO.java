package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import org.springframework.dao.DataAccessException;

/**
 * Account表的DAO操作
 * User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " account ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD_WITH_ID =
            " id, passport_id, password, mobile, reg_time, reg_ip, flag, passwordtype, account_type ,uniqname, avatar ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " passport_id, password, mobile, reg_time, reg_ip, flag, passwordtype, account_type ,uniqname, avatar ";

    /**
     * 值列表
     */
    String
            VALUE_FIELD =
            " :passport_id, :account.password, :account.mobile, :account.regTime, :account.regIp, :account.flag, " +
                    ":account.passwordtype, :account.accountType, :account.uniqname, :account.avatar ";

    /**
     * 根据passportId获取Account
     */
    @SQL("select" +
            ALL_FIELD_WITH_ID +
            "from" +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public Account getAccountByPassportId(@ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改用户密码
     */
    @SQL("update " +
            TABLE_NAME +
            " set password=:password where passport_id=:passport_id")
    public int updatePassword(@SQLParam("password") String password,
                              @ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改绑定手机
     */
    @SQL("update " +
            TABLE_NAME +
            " set mobile=:mobile where passport_id=:passport_id")
    public int updateMobile(@SQLParam("mobile") String mobile,
                            @ShardBy @SQLParam("passport_id") String passport_id) throws DataAccessException;


    /**
     * 修改昵称
     */
    @SQL("update " +
            TABLE_NAME +
            " set uniqname=:uniqname where passport_id=:passport_id")
    public int updateUniqName(@SQLParam("uniqname") String uniqname,
                              @ShardBy @SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 修改头像
     */
    @SQL("update " +
            TABLE_NAME +
            " set avatar=:avatar where passport_id=:passport_id")
    public int updateAvatar(@SQLParam("avatar") String avatar,
                            @ShardBy @SQLParam("passport_id") String passport_id) throws DataAccessException;


    /**
     * 封禁或解禁用户
     */
    @SQL("update " +
            TABLE_NAME +
            " set flag=:flag where passport_id=:passport_id")
    public int updateState(@SQLParam("flag") int flag,
                           @ShardBy @SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 验证合法，用户注册
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(" + ALL_FIELD + ") " + "values (" + VALUE_FIELD + ")")
    public int insertAccount(@ShardBy @SQLParam("passport_id") String passport_id,
                             @SQLParam("account") Account account) throws DataAccessException;

    /**
     * 验证合法，用户注册
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(" + ALL_FIELD + ") " + "values (" + VALUE_FIELD + ") on duplicate key "
                    + "update "
                    + "#if(:account.password != null){password=:account.password,} "
                    + "#if(:account.mobile != null){mobile=:account.mobile,} "
                    + "#if(:account.regTime != null){reg_time=:account.regTime,} "
                    + "#if(:account.regIp != null){reg_ip=:account.regIp,} "
                    + "#if(:account.flag > 0){flag=:account.flag,} "
                    + "#if(:account.accountType > 0){account_type=:account.accountType,} "
                    + "#if(:account.uniqname != null){uniqname=:account.uniqname,} "
                    + "#if(:account.avatar != null){avatar=:account.avatar,} "
                    + "#if(:account.passwordtype >= 0){passwordtype=:account.passwordtype} ")
    public int insertOrUpdateAccount(@ShardBy @SQLParam("passport_id") String passport_id,
                                     @SQLParam("account") Account account) throws DataAccessException;

    /**
     * 根据passportId删除用户的Account信息， 内部调试接口使用
     */
    @SQL("delete from" +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public int deleteAccountByPassportId(@ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

}
