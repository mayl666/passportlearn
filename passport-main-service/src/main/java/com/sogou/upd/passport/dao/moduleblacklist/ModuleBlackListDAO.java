package com.sogou.upd.passport.dao.moduleblacklist;

import com.sogou.upd.passport.model.black.BlackItem;
import com.sogou.upd.passport.model.moduleblacklist.ModuleBlacklist;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:17
 */
@DAO
public interface ModuleBlackListDAO {


    /**
     * 黑名单 userid、有效期
     */
    String TABLE_FILED = " userid,expire_time ";

    /**
     * 黑名单 userid、账号类型、有效期
     */
    String INSERT_TABLE_FILED = " userid,account_type,expire_time ";

    /**
     * 黑名单 属性值
     */
    String VALUE_FILED = " :moduleBlacklist.userid, :moduleBlacklist.account_type, :moduleBlacklist.expire_time ";

    String TABLE_NAME = " module_user_blacklist ";


    /**
     * 根据指定时间、获取黑名单增量数据
     */
    @SQL("select " +
            TABLE_FILED +
            " from " +
            TABLE_NAME +
            " where create_time >= :update_timestamp ")
    public ModuleBlacklist getIncreModuleBlackList(@SQLParam("update_timestamp") Date update_timestamp) throws DataAccessException;


    /**
     * 获取全量黑名单数据
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select " + TABLE_FILED + " from " + TABLE_NAME)
    public ModuleBlacklist getTotalModuleBlackList() throws DataAccessException;


    /**
     * 后台维护黑名单列表
     *
     * @param moduleBlacklist
     * @return
     * @throws DataAccessException
     */
    @SQL("insert into " + TABLE_NAME + "(" + INSERT_TABLE_FILED + ")" + " values (" + VALUE_FILED + ")")
    public int insertModuleBlacklist(@SQLParam("moduleblacklist") ModuleBlacklist moduleBlacklist) throws DataAccessException;


    /**
     * 根据黑名单userid 删除对应的黑名单信息
     *
     * @param userid
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from " + TABLE_NAME + " where userid=:userid")
    public int deleteModuleBlacklistByUserid(@SQLParam("userid") String userid) throws DataAccessException;


    /**
     * 检查黑名单usrid 是否已经存在
     *
     * @param userid
     * @return
     * @throws DataAccessException
     */
    @SQL("select " + TABLE_FILED + " from " + TABLE_NAME + " where userid=:userid")
    public int checkModuleBlacklistExist(@SQLParam("userid") String userid) throws DataAccessException;


    /**
     * 更新指定黑名单的有效期
     *
     * @param userid
     * @param expire_time
     * @return
     * @throws DataAccessException
     */
    @SQL("update " + TABLE_NAME + " set expire_time=:expire_time where userid=:userid")
    public int updateModuleBlackUserExpireTime(@SQLParam("userid") String userid, @SQLParam("expire_time") int expire_time) throws DataAccessException;
}
