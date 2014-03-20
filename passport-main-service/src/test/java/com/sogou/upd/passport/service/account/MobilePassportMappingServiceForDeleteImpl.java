package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.impl.MobilePassportMappingServiceImpl;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-20
 * Time: 下午8:28
 * To change this template use File | Settings | File Templates.
 */
public class MobilePassportMappingServiceForDeleteImpl extends MobilePassportMappingServiceImpl implements MobilePassportMappingServiceForDelete {

    @Inject
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Inject
    private DBShardRedisUtils dbShardRedisUtils;

    @Override
    public boolean deleteMobilePassportMapping(String mobile) throws ServiceException {
        try {
            int row = mobilePassportMappingDAO.deleteMobilePassportMapping(mobile);
            if (row != 0) {
                String cacheKey = buildMobilePassportMappingKey(mobile);
                dbShardRedisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }
}
