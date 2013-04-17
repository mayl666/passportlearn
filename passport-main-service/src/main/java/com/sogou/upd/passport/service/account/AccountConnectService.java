package com.sogou.upd.passport.service.account;

import java.util.List;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;

/**
 * Account_Connect表服务接口
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public interface AccountConnectService {

    /**
     * 根据query查询AccountConnect对象
     *
     * @param query
     * @return
     */
    public List<AccountConnect> listAccountConnectByQuery(AccountConnectQuery query);

    /**
     * 初始化第三方用户信息
     *
     * @param accountConnect
     * @return
     */
    public boolean initialAccountConnect(AccountConnect accountConnect);

    /**
     * 更新第三方用户信息
     *
     * @param accountConnect
     * @return
     */
    public boolean updateAccountConnect(AccountConnect accountConnect);


    /**
     * 根据userId查询Uid
     *
     * @param userId
     * @return
     */
    public String getUidByUserId(long userId);


}
