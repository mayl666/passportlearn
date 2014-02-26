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
public interface ConnectTokenService {

    /**
     * 初始化第三方用户信息
     *
     * @param connectToken
     * @return
     */
    public boolean initialConnectToken(ConnectToken connectToken) throws ServiceException;

    /**
     * 更新第三方用户信息
     *
     * @param connectToken
     * @return
     */
    public boolean updateConnectToken(ConnectToken connectToken) throws ServiceException;


    /**
     * 查询指定用户、客户端、第三方平台的openid
     * 如果获取不到，返回null
     *
     * @return
     */
    public String querySpecifyOpenId(String passportId, int provider, String appKey) throws ServiceException;

    /**
     * 获取ConnectToken对象
     *
     * @param passportId
     * @param provider
     * @param appKey
     * @return
     * @throws ServiceException
     */
    public ConnectToken queryConnectToken(String passportId, int provider, String appKey) throws ServiceException;

    /**
     * 新增或更新ConnectTokena对象
     *
     * @param connectToken
     * @return
     * @throws ServiceException
     */
    public boolean insertOrUpdateConnectToken(ConnectToken connectToken) throws ServiceException;


}
