package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public interface PCAccountService {
    public boolean checkToken(String key, String token) throws ServiceException;
}
