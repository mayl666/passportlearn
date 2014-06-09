package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.UserExtInfoTmp;
import com.sogou.upd.passport.model.account.UserInfoTmp;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Account表的DAO操作
 * User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface UserInfoTmpDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " user_info_tmp_inc0527wy ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " userid, password, passwordtype, flag ";

    /**
     * 根据passportId获取UserInfo表数据
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid=:userid")
    public UserInfoTmp getUserInfoTmpByUserid(@SQLParam("userid") String userid) throws DataAccessException;

    /**
     * 根据passportId获取UserInfo表数据
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid=:userid")
    public List<UserInfoTmp> listUserInfoTmpByUserid(@SQLParam("userid") String userid) throws DataAccessException;

    /**
     * 根据passportId删除UserInfo表数据
     */
    @SQL("delete from" + TABLE_NAME + " where userid=:userid limit :count")
    public int deleteMulUserInfoTmpByUserid(@SQLParam("userid") String userid, @SQLParam("count") int count) throws DataAccessException;

}
