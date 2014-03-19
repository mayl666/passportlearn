package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-19
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
public interface AccountManager {

    /**
     * @param passportId
     * @return
     * @throws Exception
     */
    public Account queryNormalAccount(String passportId) throws Exception;


    /**
     * 根据passportId获取Account
     */
    public Account queryAccountByPassportId(String passportId) throws ServiceException;
}
