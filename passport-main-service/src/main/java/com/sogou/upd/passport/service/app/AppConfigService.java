package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.AppConfig;

import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-25 Time: 下午11:41 To change this template
 * use File | Settings | File Templates.
 */
public interface AppConfigService {

    /**
     * 获取sms信息
     */
    public String querySmsText(int clientId, String smsCode) throws ServiceException;

    /**
     * 获取所有app_config
     * @return
     * @throws DataAccessException
     */
    public List<AppConfig> listAllAppConfig() throws ServiceException;

    /**
     * 根据ClientId 获取AppConfig （缓存读取）
     */
    public AppConfig queryAppConfigByClientId(int clientId) throws ServiceException;

    /**
     * 添加appConfig
     * @param sms_text
     * @param access_token_expiresin
     * @param refresh_token_expiresin
     * @param client_name
     * @return
     * @throws ServiceException
     */
    @Deprecated
    public boolean insertAppConfig(String sms_text, int access_token_expiresin,
                                   int refresh_token_expiresin, String client_name) throws ServiceException;

    /**
     * 添加appConfig
     * @param clientId
     * @param clientName
     * @param serverSecret
     * @param clientSecret
     * @return
     * @throws ServiceException
     */
    public boolean insertAppConfig(int clientId, String clientName, String serverSecret,
                                   String clientSecret) throws ServiceException;

    /**
     * 修改appConfig
     * @param client_id
     * @param sms_text
     * @param access_token_expiresin
     * @param refresh_token_expiresin
     * @param client_name
     * @return
     * @throws ServiceException
     */
    public boolean updateAppConfig(int client_id, String sms_text, int access_token_expiresin,
                               int refresh_token_expiresin, String client_name) throws ServiceException;

    /**
     * 修改appConfig 名字
     * @param client_id
     * @param client_name
     * @return
     * @throws ServiceException
     */
    public boolean updateAppConfigName(int client_id, String client_name) throws ServiceException;

    /**
     * 删除AppConfig
     * @param client_id
     * @return
     * @throws ServiceException
     */
    public boolean deleteAppConfig(int client_id) throws ServiceException;

    /**
     * 根据clientId获取clientName
     */
    public String queryClientName(int clientId) throws ServiceException;

    /**
     *  验证clientId和clientSecret合法性
     *  如果不合法返回null
     */
    public AppConfig verifyClientVaild(int clientId, String clientSecret);
}
