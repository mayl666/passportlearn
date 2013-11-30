package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * User: mayan
 * Date: 13-8-8
 * Time: 下午9:43
 */
@DAO
public interface UniqNamePassportMappingDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " uniqname_passportid_mapping ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, uniqname, passport_id, update_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :uniqNamePassportidMapping.id, :uniqNamePassportidMapping.nickname, :uniqNamePassportidMapping.passportId, :uniqNamePassportidMapping.updateTime ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " uniqname = :uniqNamePassportidMapping.mobile, passport_id = :uniqNamePassportidMapping.passportId, update_time = :uniqNamePassportidMapping.updateTime ";

    /**
     * 根据昵称获取passportId
     *
     * @param uniqname
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where uniqname=:uniqname")
    public String getPassportIdByUniqName(@SQLParam("uniqname") String uniqname) throws DataAccessException;

    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME)
    public List<UniqnamePassportMapping> lisPassportIdByUniqName() throws DataAccessException;

    /**
     * 插入一条mobile和passportId的映射关系
     *
     * @param uniqname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("insert into " +
            "uniqname_passportid_mapping(uniqname, passport_id) values (:uniqname, :passport_id)")
    public int insertUniqNamePassportMapping(@SQLParam("uniqname") String uniqname, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 更新mobile和passportId的映射关系
     *
     * @param uniqname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("update " +
            TABLE_NAME +
            " set passport_id=:passport_id where uniqname=:uniqname")
    public int updateUniqNamePassportMapping(@SQLParam("uniqname") String uniqname, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 删除nickname和passportId的映射关系
     *
     * @param uniqname
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where uniqname=:uniqname")
    public int deleteUniqNamePassportMapping(@SQLParam("uniqname") String uniqname) throws DataAccessException;
}
