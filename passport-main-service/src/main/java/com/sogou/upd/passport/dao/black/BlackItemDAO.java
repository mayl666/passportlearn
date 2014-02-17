package com.sogou.upd.passport.dao.black;

import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.black.BlackItem;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface BlackItemDAO {
    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " black_item ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD = " id,flag_ip,ip_or_username,flag_success_limit,insert_time,duration_time,insert_server ";

    /**
     * 新添用户
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(flag_ip,ip_or_username,flag_success_limit,insert_time,duration_time,insert_server) "
                    + "values (:blackItem.flagIp,:blackItem.ipOrUsername,:blackItem.isSuccessLimit,:blackItem.insertTime,:blackItem.durationTime,:blackItem.insertServer)")
    public int insertBlackItem(@SQLParam("blackItem") BlackItem blackItem) throws DataAccessException;
}
