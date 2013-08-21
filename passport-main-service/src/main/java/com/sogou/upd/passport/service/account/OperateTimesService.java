package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-8 Time: 下午5:38 To change this template use
 * File | Settings | File Templates.
 */
public interface OperateTimesService {

    public long recordTimes(String cacheKey,long timeout)throws ServiceException;

    /**
     * 通过hash结构，记录次数
     * @param hKey
     * @param key
     * @param timeout
     * @throws ServiceException
     */
    public void hRecordTimes(String hKey,String key, long timeout) throws ServiceException;

    public boolean checkTimesByKey(String cacheKey, final int max)throws ServiceException;

    public boolean checkTimesByKeyList(List<String> keyList, List<Integer> maxList) throws ServiceException;

    /**
     * 通过hget查询次数是否超出限制
     * @param hKey
     * @param key
     * @param max
     * @return
     * @throws ServiceException
     */
    public boolean hCheckTimesByKey(String hKey, String key, final int max) throws ServiceException;
    /**
     * 是否等于受限制次数的一半，用于记录日志
     * @param cacheKey
     * @param max
     * @return
     * @throws ServiceException
     */
    public boolean isHalfTimes(String cacheKey, final int max) throws ServiceException;

    /**
     *记录登陆成功或者失败的次数
     * @param username
     * @param ip
     * @param isSuccess
     * @throws ServiceException
     */
    public void incLoginTimes(final String username, final String ip,final boolean isSuccess) throws ServiceException;

    /**
     * 检查username是否在黑名单中
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkLoginUserInBlackList(String username,String ip) throws ServiceException;

    /**
     * 记录一天内修改密码的次数
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public long incResetPasswordTimes(String passportId) throws ServiceException;
    /**
     *检查一天内修改密码的次数
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitResetPwd(String passportId) throws ServiceException;

    /**
     * 记录一天内修改密码的次数
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incResetPwdIPTimes(String ip) throws ServiceException;

    /**
     *检查一天内修改密码的次数
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkIPLimitResetPwd(String ip) throws ServiceException;

    /**
     * 每天每IP设置密保次数
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incIPBindTimes(String ip) throws ServiceException;

    /**
     * 每天每IP设置密保次数
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkIPBindLimit(String ip) throws ServiceException;

    /**
     * 记录一天内某ip注册次数
     * @param ip
     * @return
     * @throws ServiceException
     */
    public void incRegTimes(String ip,String cookieStr) throws ServiceException;

    /**
     * 检查用户ip是否在白名单中
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkRegInWhiteList(String ip) throws ServiceException;

    /**
     *   检查一天内某ip注册次数
     */
    public boolean checkRegInBlackList(String ip,String cookieStr) throws ServiceException;

    /**
     * 联系登陆失败的次数超过限制，需要输入验证码
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean loginFailedTimesNeedCaptcha(String username,String ip) throws ServiceException;

    /**
     * 记录提及反馈次数
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incAddProblemTimes(String ip) throws ServiceException;

    /**
     * 检查提及反馈次数是否超限
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkAddProblemInBlackList(String ip) throws ServiceException;

    /**
     * 每日设置密保次数限制
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitBind(String userId, int clientId) throws ServiceException;

    /**
     * 检查设置密保次数（含邮箱、手机、问题）
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitBind(String userId, int clientId) throws ServiceException;

    /**
     * 修改密码次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitResetPwd(String userId, int clientId) throws ServiceException;

    /**
     * 验证密码失败次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitCheckPwdFail(String userId, int clientId, AccountModuleEnum module) throws ServiceException;

    /**
     * 检查修改密码次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitResetPwd(String userId, int clientId) throws ServiceException;

    /**
     * 检查验证密码失败次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitCheckPwdFail(String userId, int clientId, AccountModuleEnum module) throws ServiceException;

    /**
     * 检查用户是否在白名单列表里面
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkLoginUserInWhiteList(String username,String ip) throws ServiceException;
}
