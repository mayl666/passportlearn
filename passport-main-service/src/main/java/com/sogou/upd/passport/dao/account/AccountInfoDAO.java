package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountInfo;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import net.paoding.rose.jade.annotation.ShardBy;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-25 Time: 下午4:26 To change this template use
 * File | Settings | File Templates.
 */
@DAO
public interface AccountInfoDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " account_info ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, passport_id, email, question, answer, birthday, gender, province, city,fullname,personalid, modifyip,update_time,create_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :accountInfo.id, :accountInfo.passportId, :accountInfo.email, :accountInfo.question, :accountInfo.answer ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " passport_id = :accountInfo.passportId, email = :accountInfo.email, question = :accountInfo.question, answer = :accountInfo.answer ";

    /**
     * 根据passportId获取AccountInfo
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public AccountInfo getAccountInfoByPassportId(@ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;


    /**
     * 非第三方账号迁移，特别提示，仅供数据验证使用，根据passportId获取AccountInfo
     */
    @SQL("select " +
            " email,gender, province, city,fullname,personalid" +
            " from " +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public AccountInfo getAccountInfoByPid4DataCheck(@ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;


    /**
     * 根据passportId删除用户的AccountInfo信息，
     * 内部调试接口使用
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public int deleteAccountInfoByPassportId(@ShardBy @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改绑定邮箱，若passport_id不存在则插入新记录
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(passport_id, email, question, answer,create_time) "
                    + "values(:passport_id,:accountInfo.email,:accountInfo.question,:accountInfo.answer,now()) on duplicate key "
                    + "update email = :accountInfo.email")
    public int saveEmailOrInsert(@ShardBy @SQLParam("passport_id") String passport_id,
                                 @SQLParam("accountInfo") AccountInfo account_info)
            throws DataAccessException;

    /**
     * 修改密保问题和答案，若passport_id不存在则插入新记录
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(passport_id, email, question, answer,create_time)"
                    + "values(:passport_id,:accountInfo.email,:accountInfo.question,:accountInfo.answer,now()) on duplicate key "
                    + "update question = :accountInfo.question, answer = :accountInfo.answer")
    public int saveQuesOrInsert(@ShardBy @SQLParam("passport_id") String passport_id,
                                @SQLParam("accountInfo") AccountInfo account_info)
            throws DataAccessException;

    /**
     * 修改个人信息
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(passport_id, birthday, gender, province, city, fullname, personalid, modifyip,create_time,update_time)"
                    + "values(:passport_id,:accountInfo.birthday,:accountInfo.gender,:accountInfo.province,:accountInfo.city,:accountInfo.fullname, :accountInfo.personalid,:accountInfo.modifyip,:accountInfo.createTime,:accountInfo.updateTime) on duplicate key "
                    + "update birthday = :accountInfo.birthday, gender = :accountInfo.gender, province = :accountInfo.province, city = :accountInfo.city, fullname = :accountInfo.fullname, modifyip = :accountInfo.modifyip,create_time = :accountInfo.createTime,update_time=:accountInfo.updateTime")
    public int saveInfoOrInsert(@ShardBy @SQLParam("passport_id") String passport_id,
                                @SQLParam("accountInfo") AccountInfo account_info)
            throws DataAccessException;

    /**
     * 插入新记录
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(passport_id,email,question,answer,create_time) "
                    + "values (:passport_id,:accountInfo.email,:accountInfo.question,:accountInfo.answer,now())")
    public int insertAccountInfo(@ShardBy @SQLParam("passport_id") String passport_id,
                                 @SQLParam("accountInfo") AccountInfo account_info)
            throws DataAccessException;


}
