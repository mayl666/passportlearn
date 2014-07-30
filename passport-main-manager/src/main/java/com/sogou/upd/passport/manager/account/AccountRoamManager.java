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
     * 漫游起始端解析登录态
     *
     * @param sLoginPassportId 漫游起始端解析出来的登录userid
     * @return
     * @throws ServiceException
     */
    Result roamGo(String sLoginPassportId);

    /**
     * 支持：搜狗域、搜狐域、第三方账号 3类账号漫游
     * <p/>
     * 不支持：外域、手机账号漫游
     * <p/>
     * 账号策略： 因支持漫游测试阶段，搜狐并没有停掉漫游，所以需要做兼容逻辑处理。
     * <p/>
     * (1)对于漫游过来的手机、外域邮箱账号、直接清掉cookie
     * (2)账号在sg不存在:
     * 1、对搜狗域账号、第三方账号、直接清除掉cookie
     * 2、搜狐域账号、初始化Account、AccountInfo
     * <p/>
     * <p/>
     * 签名数据存储
     * key:sgId
     * value: version:xxxx|userid:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)|ip:xxxx(用户真实ip)
     *
     * @param response
     * @param sgLogin    搜狗是否登录
     * @param sgLgUserId 搜狗登录用户userid
     * @param r_key      签名信息
     * @param ru         调整地址
     * @param createIp   用户IP
     * @param clientId   应用id
     * @return
     * @throws ServiceException
     */
    Result webRoam(HttpServletResponse response, boolean sgLogin, String sgLgUserId, String r_key, String ru, String createIp, int clientId) throws ServiceException;
}
