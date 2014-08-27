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

    String TABLE_NAME = " module_user_blacklist ";


    /**
     * 根据指定时间、获取黑名单增量数据
     */
    @SQL("select " +
            TABLE_FILED +
            " from " +
            TABLE_NAME +
            " where expire_time >= :update_timestamp ")
    public ModuleBlacklist getIncreModuleBlackList(@SQLParam("expire_time") Date update_timestamp) throws DataAccessException;


    /**
     * 获取全量黑名单数据
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select " + TABLE_FILED + " from " + TABLE_NAME)
    public ModuleBlacklist getTotalModuleBlackList() throws DataAccessException;



}
