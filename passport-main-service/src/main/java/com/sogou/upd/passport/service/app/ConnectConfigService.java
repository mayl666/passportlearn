package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.ConnectConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午1:14
 * To change this template use File | Settings | File Templates.
 */
@Service
public interface ConnectConfigService {

    /**
     * 根据clientId和provider获得ConnectConfig对象
     * 获取passport对应的第三方appkey
     * @param provider
     * @return
     * @throws ServiceException
     */
    public ConnectConfig queryDefaultConnectConfig(int provider) throws ServiceException;

    /**
     * 根据clientId和provider获得ConnectConfig对象
     * 获取passport对应的第三方appkey
     * @param provider
     * @return
     * @throws ServiceException
     */
    public ConnectConfig queryConnectConfigByAppId(String appId, int provider) throws ServiceException;

    /**
     * 根据clientId和provider获得ConnectConfig对象
     * 如果获取不到，返回null
     * @param clientId
     * @param provider
     * @return
     * @throws ServiceException
     */
    public ConnectConfig queryConnectConfigByClientId(int clientId, int provider) throws ServiceException;

    public List<ConnectConfig> ListConnectConfigByClientId(int clientId) throws ServiceException;

    /**
     * 更新ConnectConfig
     * @param connectConfig
     * @return
     * @throws ServiceException
     */
    public boolean modifyConnectConfig(ConnectConfig connectConfig) throws ServiceException;
}
