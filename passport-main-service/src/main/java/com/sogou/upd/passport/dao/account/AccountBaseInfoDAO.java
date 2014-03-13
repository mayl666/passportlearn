package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountBaseInfo;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * User: mayan
 * Date: 13-11-27
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface AccountBaseInfoDAO {
    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " account_base_info ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " id,passport_id,uniqname,avatar ";

    /**
     * 根据passportId获取Account
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public AccountBaseInfo getAccountBaseInfoByPassportId(@SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 每次取固定条数的第三方账号的记录
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where passport_id LIKE \'%@%.sohu.com\' LIMIT :pageIndex,:pageSize")
    public List<AccountBaseInfo> listConnectBaseInfoByPage(@SQLParam("pageIndex") int pageIndex,
                                                           @SQLParam("pageSize") int pageSize) throws DataAccessException;

    /**
     * 修改头像信息
     */
    @SQL("update " +
            TABLE_NAME +
            " set avatar=:avatar where passport_id=:passport_id")
    public int updateAvatarByPassportId(@SQLParam("avatar") String avatar,
                                        @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 修改昵称信息
     */
    @SQL("update " +
            TABLE_NAME +
            " set uniqname=:uniqname where passport_id=:passport_id")
    public int updateUniqnameByPassportId(@SQLParam("uniqname") String uniqname,
                                          @SQLParam("passport_id") String passport_id) throws
            DataAccessException;

    /**
     * 新添用户
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(passport_id,uniqname,avatar) "
                    + "values (:passport_id,:baseInfo.uniqname,:baseInfo.avatar)")
    public int insertAccountBaseInfo(@SQLParam("passport_id") String passport_id,
                                     @SQLParam("baseInfo") AccountBaseInfo baseInfo) throws DataAccessException;

    /**
     * 往用户状态表中插入一条记录
     * 有多个唯一索引时不要使用，存在则更新，不存在则插入
     */
    @SQL("insert into " +
            TABLE_NAME +
            "(passport_id,uniqname,avatar) values (:passport_id,:baseInfo.uniqname,:baseInfo.avatar) "
            + "ON DUPLICATE KEY UPDATE "
            + "passport_id=:passport_id,uniqname=:baseInfo.uniqname,avatar=:baseInfo.avatar")
    public int saveAccountBaseInfo(@SQLParam("passport_id") String passport_id, @SQLParam("baseInfo") AccountBaseInfo baseInfo) throws
            DataAccessException;

    /**
     * 计算第三方账号总数
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select count(*) from" +
            TABLE_NAME + "where passport_id LIKE \'%@%.sohu.com\'")
    public int getConnectTotalCount() throws DataAccessException;


}
