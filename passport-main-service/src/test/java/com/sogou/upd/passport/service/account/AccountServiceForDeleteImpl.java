package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.impl.AccountServiceImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-20
 * Time: 下午8:18
 * To change this template use File | Settings | File Templates.
 */
public class AccountServiceForDeleteImpl extends AccountServiceImpl implements AccountServiceForDelete {

    @Inject
    private AccountDAO accountDAO;
    @Inject
    private RedisUtils redisUtils;

    @Override
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException {
        try {
            int row = accountDAO.deleteAccountByPassportId(passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }


}
