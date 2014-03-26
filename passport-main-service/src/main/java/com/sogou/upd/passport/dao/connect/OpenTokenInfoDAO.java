package com.sogou.upd.passport.dao.connect;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * connect_token表的DAO操作
 * User: shipengzhi
 * Date: 13-4-18 Time:
 * 下午3:34
 */
@DAO
public interface OpenTokenInfoDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " open_token_info_utf8 ";

    /**
     * 所有字段列表
     */
    String ALL_FIELD = " token, secret, platform, refuserid, authed, createtime, appid, expire_time, refresh ";

    /**
     * 从搜狐导出临时表根据唯一键获取记录
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where "
            + "(refuserid=:refuserid and platform=:platform and appid=:appid)")
    public OpenTokenInfo getOpenTokenInfo(@SQLParam("refuserid") String refuserid, @SQLParam("platform") String platform,
                                          @SQLParam("appid") String appid) throws DataAccessException;

}
