package com.sogou.upd.passport.dao.account;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-26
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */

import com.sogou.upd.passport.model.account.AccountAuth;

import java.util.Map;

/**
 * 用户状态表的接口mapper文件
 */
public interface AccountAuthMapper {
    /**
     * 往用户状态表中插入一条记录
     * @param accountAuth
     * @return
     */
    public int saveAccountAuth(AccountAuth accountAuth);

    /**
     * 更新用户状态表
     * @param accountAuth
     * @return
     */
    public int updateAccountAuth(AccountAuth accountAuth);
}
