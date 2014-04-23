package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;
import org.springframework.dao.DataAccessException;

import java.sql.Timestamp;
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
    public String getPassportIdByUniqName(@ShardBy @SQLParam("uniqname") String uniqname) throws DataAccessException;


    /**
     * u_p_m 主表分成32张子表，分页查询u_p_m 主表数据
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DataAccessException
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME + "LIMIT :pageIndex,:pageSize")
    public List<UniqnamePassportMapping> getUpmDataByPage(@SQLParam("pageIndex") int pageIndex,
                                                          @SQLParam("pageSize") int pageSize) throws DataAccessException;


    /**
     * u_p_m 主表分成32张子表,导入程序使用, 查询u_p_m 主表数据总数
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select count(*) from " + TABLE_NAME)
    public int getUpmTotalCount() throws DataAccessException;


    /**
     * u_p_m 主表分成32张子表,导入程序使用, 根据 passport_id hash 数据到具体子表中
     *
     * @param uniqname
     * @param passport_id
     * @param update_time
     * @return
     * @throws DataAccessException
     */
    @SQL("insert into "
            + TABLE_NAME + "(uniqname, passport_id,update_time) values (:uniqname, :passport_id,:update_time)")
    public int insertUpm0To32(@ShardBy @SQLParam("uniqname") String uniqname,
                              @SQLParam("passport_id") String passport_id,
                              @SQLParam("update_time") Timestamp update_time) throws DataAccessException;


    /**
     * u_p_m 主表分成32张子表，导入程序使用,根据　passport_id　查询子表中是否已经存在
     *
     * @param uniqname
     * @return
     */
    @SQL("select " + ALL_FIELD + " from " + TABLE_NAME + " where uniqname=:uniqname")
    public UniqnamePassportMapping getUpmByPassportId(@ShardBy @SQLParam("uniqname") String uniqname);


    /**
     * 插入一条mobile和passportId的映射关系
     * <p/>
     * u_p_m 分32张表，根据passport_id进行hash
     *
     * @param uniqname
     * @param passport_id
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("insert into " +
            TABLE_NAME + "(uniqname, passport_id) values (:uniqname, :passport_id)")
    public int insertUniqNamePassportMapping(@ShardBy @SQLParam("uniqname") String uniqname, @SQLParam("passport_id") String passport_id)
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
    public int deleteUniqNamePassportMapping(@ShardBy @SQLParam("uniqname") String uniqname) throws DataAccessException;
}
