package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.app.ConnectConfig;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午1:10
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface ConnectConfigDAO {

    @SQL("select * from connect_config where client_id=:client_id and provider=:provider")
    public ConnectConfig getConnectConfigByClientIdAndProvider(@SQLParam("client_id") int client_id, @SQLParam("provider") int provider) throws
            DataAccessException;
}
