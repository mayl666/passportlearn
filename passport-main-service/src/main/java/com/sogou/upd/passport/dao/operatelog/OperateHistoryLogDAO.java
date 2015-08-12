package com.sogou.upd.passport.dao.operatelog;

import com.sogou.upd.passport.model.operatelog.OperateHistoryLog;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * 后台操作记录历史
 * User: chengang
 * Date: 14-8-8
 * Time: 下午3:07
 */
@DAO
public interface OperateHistoryLogDAO {


    /**
     * 表名
     */
    String TABLE_NAME = " operate_history_log ";

    /**
     * 数据项
     */
    public static final String COLUMN = " operate_userid,operate_userip,operate_user,account_type,operate_type,operate_before_status,operate_after_status ";


    /**
     * 数据值
     */
    public static final String VALUE_FIELD = " :operateHistoryLog.operate_userid,:operateHistoryLog.operate_userip,:operateHistoryLog.operate_user,:operateHistoryLog.account_type," +
            " :operateHistoryLog.operate_type,:operateHistoryLog.operate_before_status,:operateHistoryLog.operate_after_status";


    /**
     * 记录后台操作记录
     *
     * @param operateHistoryLog
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    @SQL(" insert into " + TABLE_NAME + "(" + COLUMN + ")" + " values (" + VALUE_FIELD + ")")
    public int insertOperateHistoryLog(@SQLParam("operateHistoryLog") OperateHistoryLog operateHistoryLog) throws DataAccessException;


}
