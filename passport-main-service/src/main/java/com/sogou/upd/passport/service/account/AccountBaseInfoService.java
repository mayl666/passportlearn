package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

/**
 * TODO 搜狗账号迁移后，搜狗账号昵称头像写入account表，其他账号写入account_base_info表
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 上午1:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountBaseInfoService {

    /**
     * 初始化第三方个人资料
     *
     * @param passportId
     * @param connectUserInfoVO
     * @param isAsync           是否异步
     */
    public void initConnectAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO, boolean isAsync);

    /**
     * 获取个人资料
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public AccountBaseInfo queryAccountBaseInfo(String passportId) throws ServiceException;

    /**
     * 未检查昵称是否唯一，默认是唯一的
     * 更新昵称表，更新映射表
     *
     * @param baseInfo
     * @param uniqname
     */
    public boolean updateUniqname(AccountBaseInfo baseInfo, String uniqname);

    /**
     * 插入昵称和头像，默认用户原来不存在昵称头像
     *
     * @param passportId
     * @param uniqname
     * @param avatar
     * @return
     */
    public boolean insertAccountBaseInfo(String passportId, String uniqname, String avatar);

    /**
     * 插入昵称和头像
     * 不做任何昵称和头像逻辑判断，只是存在则更新，不存在则插入
     *
     * @param accountBaseInfo
     * @return
     * @throws ServiceException
     */
    public boolean simpleSaveAccountBaseInfo(AccountBaseInfo accountBaseInfo);

}
