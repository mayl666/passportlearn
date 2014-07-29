package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;

import javax.servlet.http.HttpServletResponse;

/**
 * 账号漫游manager
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:26
 */
public interface AccountRoamManager {


    /**
     * 支持搜狗域、搜狐域、第三方账号漫游
     *
     * @param response
     * @param sgLogin    搜狗是否登录
     * @param sgLgUserId 在搜狗登录的userid
     * @param r_key      签名
     * @param ru         调整地址
     * @param clientId   应用id
     * @return
     * @throws ServiceException
     */
    Result webRoam(HttpServletResponse response, boolean sgLogin, String sgLgUserId, String r_key, String ru, int clientId) throws ServiceException;
}
