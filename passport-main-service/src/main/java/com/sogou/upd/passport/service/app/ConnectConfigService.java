package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.ConnectConfig;
import org.springframework.stereotype.Service;

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
     * 如果获取不到，返回null
     * @param clientId
     * @param provider
     * @return
     * @throws ServiceException
     */
    public ConnectConfig querySpecifyConnectConfig(int clientId, int provider) throws ServiceException;

    /**
     * 指定根据clientId和provider获得对应的appkey
     * 如果获取不到，返回null
     * @param clientId
     * @param provider
     * @return
     * @throws ServiceException
     */
    public String querySpecifyAppKey(int clientId, int provider) throws ServiceException;

    /**
     * 更新ConnectConfig
     * @param connectConfig
     * @return
     * @throws ServiceException
     */
    public boolean modifyConnectConfig(ConnectConfig connectConfig) throws ServiceException;
}
