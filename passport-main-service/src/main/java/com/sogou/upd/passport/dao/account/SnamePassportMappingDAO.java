package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.SnamePassportMapping;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:43
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface SnamePassportMappingDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " sname_passportid_mapping ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, sid, sname, passport_id, mobile, update_time ";

    /**
     * 根据sohu+个性账号或者手机号码获取passportId
     *
     * @param sname
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where sname=:sname")
    public String getPassportIdBySname(@SQLParam("sname") String sname) throws DataAccessException;

    /**
     * 根据sid获取passportId
     *
     * @param sid
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where sid=:sid")
    public String getPassportIdBySid(@SQLParam("sid") String sid) throws DataAccessException;

    /**
     * 根据sid获取passportId
     *
     * @param mobile
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select passport_id from " +
            TABLE_NAME +
            " where mobile=:mobile")
    public String getPassportIdByMobile(@SQLParam("mobile") String mobile) throws DataAccessException;

    /**
     * 插入一条sname和passportId的映射关系
     *
     * @param sname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("insert into " + TABLE_NAME +
            "(sid, sname, passport_id, mobile) values (:sid, :sname, :passport_id, :mobile)")
    public int insertSnamePassportMapping(@SQLParam("sid") String sid, @SQLParam("sname") String sname, @SQLParam("passport_id") String passport_id, @SQLParam("mobile") String mobile)
            throws DataAccessException;

    /**
     * 更新sname和passportId的映射关系
     *
     * @param sname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("update " +
            TABLE_NAME +
            " set passport_id=:passport_id where sname=:sname")
    public int updateSnamePassportMapping(@SQLParam("sname") String sname, @SQLParam("passport_id") String passport_id)
            throws DataAccessException;

    /**
     * 删除sname和passportId的映射关系
     *
     * @param sname
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("delete from " +
            TABLE_NAME +
            " where sname=:sname")
    public int deleteSnamePassportMapping(@SQLParam("sname") String sname) throws DataAccessException;

}
