package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;

import java.util.List;

public interface AccountConnectMapper {

    /**
     * 插入一条新记录
     * @param accountConnect
     * @return
     */
	public int insertAccountConnect(AccountConnect accountConnect);

    /**
     * 根据query查询AccountConnect
     * @param query
     * @return
     */
	public List<AccountConnect> getAccountConnectByQuery(AccountConnectQuery query);

    /**
     * 更新AccountConnect
     * @param accountConnect
     * @return
     */
	public int updateAccountConnect(AccountConnect accountConnect);
}
