package com.sogou.upd.passport.dao.repairdata;

import com.sogou.upd.passport.model.repairdata.IncUserExtInfo;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface IncUserExtInfoHisDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " inc_user_ext_info_his ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " inc_type, userid, question, answer, username, gender, createip ";

    /**
     * 根据passportId获取Account
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid=:userid  order by inc_id desc limit 1 ")
    public IncUserExtInfo getIncUserExtInfo(@SQLParam("userid") String userid) throws DataAccessException;
}
