package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-20
 * Time: 下午8:27
 * To change this template use File | Settings | File Templates.
 */
public interface MobilePassportMappingServiceForDelete extends MobilePassportMappingService {

    /**
     * 删除mobile和passportId的映射关系
     *
     * @param mobile
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public boolean deleteMobilePassportMapping(String mobile) throws ServiceException;


}
