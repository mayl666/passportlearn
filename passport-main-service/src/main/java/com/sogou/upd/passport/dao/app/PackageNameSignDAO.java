package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.PackageNameSign;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: nahongxu Date: 15-2-15 Time: 下午15:43 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface PackageNameSignDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " package_name_sign ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " id, client_id, package_name, package_sign, update_time ,create_time";

    /**
     * 值列表
     */
    String VALUE_FIELD = " :packageNameSign.id, :packageNameSign.clientId, :packageNameSign.packageName, :packageNameSign.packageSign, :packageNameSign.updateTime,:packageNameSign.createTime";

    /**
     * 修改字段列表
     */
    String UPDATE_FIELD = " client_id = :packageNameSign.clientId, package_name = :packageNameSign.packageName, package_sign = :packageNameSign.packageSign, update_time = :packageNameSign.updateTime ,create_time=:packageNameSign.createTime";

    /**
     * 根据包名获取PackageSign对象
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where package_name=:packageName")
    public PackageNameSign getPackageNameSignByname(@SQLParam("packageName") String package_name) throws DataAccessException;

    /**
     * 验证合法，用户注册
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(" + ALL_FIELD + ") " + "values (" + VALUE_FIELD + ")")
    public int insertPackageNameSign(@SQLParam("packageNameSign") PackageNameSign packageNameSign) throws DataAccessException;


}
