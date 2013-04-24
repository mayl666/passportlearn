package com.sogou.upd.passport.dao.account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
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
     * 根据手机号码获取passportId
     *
     * @param mobile
     * @return 获取不到则抛出异常
     * @throws DataAccessException
     */
    @SQL("select passport_id from mobile_passportid_mapping where mobile=:mobile")
    public String getPassportIdByMobile(@SQLParam("mobile") String mobile) throws DataAccessException;

    /**
     * 插入一条mobile和passportId的映射关系
     *
     * @param mobile
     * @param passport_id
     * @return
     * @throws DataAccessException
     */
    @SQL("insert into mobile_passportid_mapping(mobile, passport_id) values (:mobile, :passport_id)")
    public int insertMobilePassportMapping(@SQLParam("mobile") String mobile, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 更新mobile和passportId的映射关系
     *
     * @param mobile
     * @param passport_id
     * @return
     * @throws DataAccessException
     */
    @SQL("update mobile_passportid_mapping set passport_id=:passport_id where mobile=:mobile")
    public int updateMobilePassportMapping(@SQLParam("mobile") String mobile, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 删除mobile和passportId的映射关系
     *
     * @param mobile
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from mobile_passportid_mapping where mobile=:mobile")
    public int deleteMobilePassportMapping(@SQLParam("mobile") String mobile) throws DataAccessException;
}
