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

    public boolean checkTimesByKey(String cacheKey, final int max)throws ServiceException;

    public boolean checkTimesByKeyList(List<String> keyList, List<Integer> maxList) throws ServiceException;

    /**
     * 是否等于受限制次数的一半，用于记录日志
     * @param cacheKey
     * @param max
     * @return
     * @throws ServiceException
     */
    public boolean isHalfTimes(String cacheKey, final int max) throws ServiceException;
    /**
     *记录一小时内登陆成功次数
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incLoginSuccessTimes(String username,String ip)throws ServiceException;

    /**
     * 记录一小时内登陆失败次数
     * @param username
     * @param ip
     * @return
     */
    public long incLoginFailedTimes(String username,String ip)throws ServiceException;

    /**
     * 检查username是否在黑名单中
     * @param username
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
     * 记录一天内某ip注册次数
     * @param ip
     * @return
     * @throws ServiceException
     */
    public void incRegTimes(String ip,String cookieStr) throws ServiceException;

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
     * 绑定密保邮箱次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitBindEmail(String userId, int clientId) throws ServiceException;

    /**
     * 绑定密保手机次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitBindMobile(String userId, int clientId) throws ServiceException;

    /**
     * 绑定密保问题次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean incLimitBindQues(String userId, int clientId) throws ServiceException;

    /**
     * 检查绑定密保邮箱次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitBindEmail(String userId, int clientId) throws ServiceException;

    /**
     * 检查绑定密保手机次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitBindMobile(String userId, int clientId) throws ServiceException;

    /**
     * 检查绑定密保问题次数
     *
     * @param userId
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitBindQues(String userId, int clientId) throws ServiceException;

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
}
