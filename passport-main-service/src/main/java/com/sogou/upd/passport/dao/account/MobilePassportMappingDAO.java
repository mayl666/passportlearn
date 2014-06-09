package com.sogou.upd.passport.dao.account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:43
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface MobilePassportMappingDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " mobile_passportid_mapping ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, mobile, passport_id, update_time ";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :mobilePassportidMapping.id, :mobilePassportidMapping.mobile, :mobilePassportidMapping.passportId, :mobilePassportidMapping.updateTime ";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " mobile = :mobilePassportidMapping.mobile, passport_id = :mobilePassportidMapping.passportId, update_time = :mobilePassportidMapping.updateTime ";

    /**
     * 根据手机号码获取passportId
     *
     * @param mobile
     * @return 获取不到则抛出异常
     * @throws DataAccessException
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where mobile=:mobile")
    public String getPassportIdByMobile(@ShardBy @SQLParam("mobile") String mobile) throws DataAccessException;

    /**
     * 插入一条mobile和passportId的映射关系
     *
     * @param mobile
     * @param passport_id
     * @return
     * @throws DataAccessException
     */
    @SQL("insert into " +
            "mobile_passportid" +
            "_mapping(mobile, passport_id) values (:mobile, :passport_id)")
    public int insertMobilePassportMapping(@ShardBy @SQLParam("mobile") String mobile, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 更新mobile和passportId的映射关系
     *
     * @param mobile
     * @param passport_id
     * @return
     * @throws DataAccessException
     */
    @SQL("update " +
            TABLE_NAME +
            " set passport_id=:passport_id where mobile=:mobile")
    public int updateMobilePassportMapping(@ShardBy @SQLParam("mobile") String mobile, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 删除mobile和passportId的映射关系
     *
     * @param mobile
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where mobile=:mobile")
    public int deleteMobilePassportMapping(@ShardBy @SQLParam("mobile") String mobile) throws DataAccessException;
}
