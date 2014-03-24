package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-20
 * Time: 下午8:21
 * To change this template use File | Settings | File Templates.
 */
public interface AccountServiceForDelete extends AccountService {

    /**
     * 自测使用
     *
     * @param passportId
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     */
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException;

}
