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
     */
    public AccountBaseInfo initConnectAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO);

    /**
     * 获取个人资料
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public AccountBaseInfo queryAccountBaseInfo(String passportId) throws ServiceException;

    /**
     * 先昵称是否唯一
     * 更新昵称表，更新映射表
     *
     * @param oldBaseInfo
     * @param uniqname 更新的昵称
     */
    public boolean updateUniqname(AccountBaseInfo oldBaseInfo, String uniqname);

    /**
     * 未检查昵称是否唯一，默认是唯一的
     * 更新昵称表，更新映射表
     *
     * @param oldBaseInfo
     * @param avatar 更新的头像
     */
    public boolean updateAvatar(AccountBaseInfo oldBaseInfo, String avatar);

    /**
     * 插入昵称和头像，默认用户原来不存在昵称头像
     *
     * @param passportId
     * @param uniqname
     * @param avatar
     * @return
     */
    public AccountBaseInfo insertAccountBaseInfo(String passportId, String uniqname, String avatar);

    /**
     * 检查昵称是否才能在
     * @param uniqname
     * @return
     */
    public boolean isUniqNameExist(String uniqname);

}
