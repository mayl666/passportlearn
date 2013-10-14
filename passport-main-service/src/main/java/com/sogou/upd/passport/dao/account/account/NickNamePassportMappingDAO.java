package com.sogou.upd.passport.dao.account.account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * User: mayan
 * Date: 13-8-8
 * Time: 下午9:43
 */
@DAO
public interface NickNamePassportMappingDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " nickname_passportid_mapping ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, nickname, passport_id, update_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :nicknamePassportidMapping.id, :nicknamePassportidMapping.nickname, :nicknamePassportidMapping.passportId, :nicknamePassportidMapping.updateTime ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " nickname = :nicknamePassportidMapping.mobile, passport_id = :nicknamePassportidMapping.passportId, update_time = :nicknamePassportidMapping.updateTime ";

    /**
     * 根据昵称获取passportId
     *
     * @param nickname
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where nickname=:nickname")
    public String getPassportIdByNickName(@SQLParam("nickname") String nickname) throws DataAccessException;

    /**
     * 插入一条mobile和passportId的映射关系
     *
     * @param nickname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    @SQL("insert into " +
            "nickname_passportid" +
            "_mapping(nickname, passport_id) values (:nickname, :passport_id)")
    public int insertNickNamePassportMapping(@SQLParam("nickname") String nickname, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 更新mobile和passportId的映射关系
     *
     * @param nickname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    @SQL("update " +
            TABLE_NAME +
            " set passport_id=:passport_id where nickname=:nickname")
    public int updateNickNamePassportMapping(@SQLParam("nickname") String nickname, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 删除nickname和passportId的映射关系
     *
     * @param nickname
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where nickname=:nickname")
    public int deleteNickNamePassportMapping(@SQLParam("nickname") String nickname) throws DataAccessException;
}
