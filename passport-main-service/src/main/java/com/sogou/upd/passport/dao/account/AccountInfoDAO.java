package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountInfo;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-25 Time: 下午4:26 To change this template use
 * File | Settings | File Templates.
 */
@DAO
public interface AccountInfoDAO {

    /**
     * 根据passportId获取AccountInfo
     */
    @SQL("select * from account_info where passport_id=:passport_id")
    public AccountInfo getAccountInfoByPassportId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 根据passportId删除用户的AccountInfo信息，
     * 内部调试接口使用
     */
    @SQL("delete from account_info where passport_id=:passport_id")
    public int deleteAccountInfoByPassportId(@SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改绑定邮箱，若passport_id不存在则插入新记录
     */
    @SQL(
            "insert into account_info(passport_id, email, question, answer)"
            + "values(:passport_id,:a.email,:a.question,:a.answer) on duplicate key"
            + "update email = :a.email")
    public int saveEmailOrInsert(@SQLParam("passport_id") String passport_id,
                                   @SQLParam("a") AccountInfo account_info)
            throws DataAccessException;

    /**
     * 修改密保问题和答案，若passport_id不存在则插入新记录
     */
    @SQL(
            "insert into account_info(passport_id, email, question, answer)"
            + "values(:passport_id,:a.email,:a.question,:a.answer) on duplicate key"
            + "update question = :a.question, answer = a.answer")
    public int saveQuesOrInsert(@SQLParam("passport_id") String passport_id,
                                  @SQLParam("a") AccountInfo account_info)
            throws DataAccessException;

    /**
     * 插入新记录
     */
    @SQL(
            "insert into account_info(passport_id,email,question,answer) "
            + "values (:passport_id,:a.email,:a.question,:a.answer)")
    public int insertAccountInfo(@SQLParam("passport_id") String passport_id,
                                 @SQLParam("a") AccountInfo account_info)
            throws DataAccessException;

}
