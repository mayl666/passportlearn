package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.UserOtherInfoTmp;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Account表的DAO操作
 * User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface UserOtherInfoTmpDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " user_other_info_tmp ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " userid, personalid, mobile, mobileflag, email, emailflag, province, uniqname, city ";

    /**
     * 值列表
     */
    String
            VALUE_FIELD =
            " :userid, :userOtherInfoTmp.personalid, :userOtherInfoTmp.mobile, :userOtherInfoTmp.mobileflag, " +
                    ":userOtherInfoTmp.email, :userOtherInfoTmp.emailflag, :userOtherInfoTmp.province, " +
                    ":userOtherInfoTmp.uniqname, :userOtherInfoTmp.city ";

    /**
     * 根据passportId获取Account
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid = :userid")
    public UserOtherInfoTmp getUserOtherInfoTmpByUserid(@SQLParam("userid") String userid) throws DataAccessException;

    /**
     * 验证合法，用户注册
     */
    @SQL("update " + TABLE_NAME + " set province=:province, city=:city where userid=:userid")
    public int updateProvinceAndCity(@SQLParam("userid") String userid, @SQLParam("province") String province, @SQLParam("city") String city)
            throws DataAccessException;

}
