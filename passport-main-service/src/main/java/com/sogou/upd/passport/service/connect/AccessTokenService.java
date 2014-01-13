package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectToken;

/**
 * Account_Connect表服务接口
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public interface AccessTokenService {

    /**
     * key:userid;valuse:accesstoken
     * @param userid
     * @param Accesstoken
     * @return
     * @throws ServiceException
     */
    public boolean initialOrUpdateAccessToken(String userid,String Accesstoken,int expire) throws ServiceException;

    /**
     * 通过userid获取accesstoken
     * @param userid
     * @return
     * @throws ServiceException
     */
    public String getAccessToken(String userid) throws ServiceException;


}
