package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.ActionRecord;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-21 Time: 上午10:51 To change this template
 * use File | Settings | File Templates.
 */
public interface AccountSecureService {

    /**
     * 产生secureCode，放入缓存，设置有效时间；返回secureCode——重置密码
     *
     * @param passportId
     * @param clientId
     * @param cacheKey
     * @return
     * @throws ServiceException
     */
    public String getSecureCode(String passportId, int clientId, String cacheKey) throws ServiceException;

    /**
     * 检测secureCode——重置密码
     *
     * @param passportId
     * @param clientId
     * @param secureCode
     * @param cacheKey
     * @return
     * @throws ServiceException
     */
    public boolean checkSecureCode(String passportId, int clientId, String secureCode, String cacheKey)
            throws ServiceException;

    /**
     * 产生secureCode，放入缓存，设置有效时间；返回secureCode——修改密保内容
     *
     * @param passportId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public String getSecureCodeModSecInfo(String passportId, int clientId) throws ServiceException;

    /**
     * 检测secureCode——记录前一步操作成功
     *
     * @param passportId
     * @param clientId
     * @param secureCode
     * @return
     * @throws ServiceException
     */
    public boolean checkSecureCodeModSecInfo(String passportId, int clientId, String secureCode)
            throws ServiceException;

    /**
     * 通过flag获取scode，同时将scode当作key存入缓存，value为flag
     *
     * @param flag
     * @return
     * @throws ServiceException
     */
    public String getSecureCodeRandom(String flag) throws ServiceException;

    /**
     * 检查缓存，scode为key，flag为value
     *
     * @param scode
     * @param flag
     * @return
     * @throws ServiceException
     */
    public boolean checkSecureCodeRandom(String scode, String flag) throws ServiceException;


    /**
     * 通过ActionRecord设置动作记录
     *
     * @param actionRecord
     */
    public void setActionRecord(ActionRecord actionRecord);

    /**
     * 获取动作记录List
     *
     * @param userId
     * @param clientId
     * @param action
     * @return
     */
    public List<ActionStoreRecordDO> getActionStoreRecords(String userId, int clientId, AccountModuleEnum action);

    /**
     * 获取最近一次动作记录
     *
     * @param userid
     * @param clientId
     * @param action
     * @return
     */
    public ActionStoreRecordDO getLastActionStoreRecord(String userid, int clientId,
                                                        AccountModuleEnum action);

}
