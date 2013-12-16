package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.SohuplusTmp;
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
public interface SohuplusTmpDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " sohuplus_tmp ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " sid, sname, passport_id, uniqname, avatar ";


    /**
     * @return 获取不到则抛出异常
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where (uniqname!= '' OR avatar != '') AND uniqname NOT LIKE '%搜狐网友%' AND uniqname NOT LIKE '%在搜狐%' AND uniqname NOT LIKE '%的blog%' ")
    public List<SohuplusTmp> listSohuplusTmp() throws DataAccessException;

    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where passport_id=:passport_id")
    public SohuplusTmp getSohuplusTmpByPassportId(@SQLParam("passport_id") String passport_id) throws DataAccessException;

}
