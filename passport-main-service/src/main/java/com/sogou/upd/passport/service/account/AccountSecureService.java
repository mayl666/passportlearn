package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.ActionRecord;

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
     * @return
     * @throws ServiceException
     */
    public String getSecureCodeResetPwd(String passportId, int clientId) throws ServiceException;

    /**
     * 检测secureCode——重置密码
     *
     * @param passportId
     * @param clientId
     * @param secureCode
     * @return
     * @throws ServiceException
     */
    public boolean checkSecureCodeResetPwd(String passportId, int clientId, String secureCode)
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

    public void setActionRecord(String userId, int clientId, AccountModuleEnum action, String ip, String note);

    public void setActionRecord(ActionRecord actionRecord);

    public List<ActionRecord> getActionRecords(String userId, int clientId, AccountModuleEnum action);

}
