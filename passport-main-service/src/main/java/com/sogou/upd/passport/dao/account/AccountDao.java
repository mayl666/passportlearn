package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-22
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public interface AccountDao {
    /**
     * 查询某手机用户是否已经注册
     * @param account 封装的对象
     * @return
     */
    public boolean checkIsRegisterAccount(Account account);

    /**
     * 用户注册
     * @param account  封装的对象
     */
    public void userRegister(Account account);
}
