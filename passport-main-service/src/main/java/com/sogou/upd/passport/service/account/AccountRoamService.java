package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountRoamInfo;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游service
 * User: chengang
 * Date: 14-7-29
 * Time: 上午11:37
 */
public interface AccountRoamService {


    /**
     * 根据sgId 获取漫游用户信息
     *
     * @param r_key
     * @return
     * @throws ServiceException
     */
    public AccountRoamInfo getAccountRoamInfoBySgId(final String r_key) throws ServiceException;


    /**
     * 根据sgId 清除漫游用户信息
     *
     * @param r_key
     * @return
     * @throws ServiceException
     */
    public void clearAccountRoamInfoBySgId(final String r_key) throws ServiceException;
}
