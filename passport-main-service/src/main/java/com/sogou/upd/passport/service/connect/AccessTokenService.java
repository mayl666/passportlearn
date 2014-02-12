package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectToken;

/**
 * 用于缓存从搜狐获取的第三方accesstoken
 * User: chenjiameng
 * Date: 13-3-24
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public interface AccessTokenService {

    /**
     * 通过userid获取accesstoken
     * @param userid
     * @return
     * @throws ServiceException
     */
    public String getAccessToken(String userid) throws ServiceException;


}
