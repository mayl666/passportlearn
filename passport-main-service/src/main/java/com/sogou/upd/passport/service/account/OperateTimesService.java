package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-8 Time: 下午5:38 To change this template use
 * File | Settings | File Templates.
 */
public interface OperateTimesService {

    public long recordTimes(String cacheKey, long timeout) throws ServiceException;

    /**
     * 通过hash结构，记录次数
     *
     * @param hKey
     * @param key
     * @param timeout
     * @throws ServiceException
     */
    public void hRecordTimes(String hKey, String key, long timeout) throws ServiceException;

    public boolean checkTimesByKey(String cacheKey, final int max) throws ServiceException;

    public boolean checkTimesByKeyList(List<String> keyList, List<Integer> maxList) throws ServiceException;

    /**
     * 通过hget查询次数是否超出限制
     *
     * @param hKey
     * @param key
     * @param max
     * @return
     * @throws ServiceException
     */
    public boolean hCheckTimesByKey(String hKey, String key, final int max) throws ServiceException;

    /**
     * 记录登陆成功或者失败的次数
     *
     * @param username
     * @param ip
     * @param isSuccess
     * @throws ServiceException
     */
    public void incLoginTimes(final String username, final String ip, final boolean isSuccess) throws ServiceException;

    /**
     * 记录一天内修改密码的次数
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incResetPwdIPTimes(String ip) throws ServiceException;

    /**
     * 检查一天内修改密码的次数
     *
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
     * 内部接口注册的ip次数累加
     *
     * @param ip
     * @throws ServiceException
     */
    public void incRegTimesForInternal(final String ip, int clientId) throws ServiceException;

    /**
     * 记录一天内某ip注册次数
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public void incRegTimes(String ip, String cookieStr) throws ServiceException;

    /**
     * 为内部接口添加的黑名单机制
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkRegInBlackListForInternal(String ip, int clientId) throws ServiceException;


    /**
     * 内部接口 检查用户是否存在安全机制
     *
     * @param ip
     * @param username
     * @return
     */
    public boolean checkUserInBlackListForInternal(final String ip, final String username);


    /**
     * 检查用户ip是否在白名单中
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkRegInWhiteList(String ip) throws ServiceException;

    /**
     * 检查一天内某ip注册次数
     */
    public boolean checkRegInBlackList(String ip, String cookieStr) throws ServiceException;

    /**
     * 联系登陆失败的次数超过限制，需要输入验证码
     *
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean loginFailedTimesNeedCaptcha(String username, String ip) throws ServiceException;

    /**
     * 记录提及反馈次数
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public long incAddProblemTimes(String ip) throws ServiceException;

    /**
     * 检查提及反馈次数是否超限
     *
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
    public boolean checkBindLimit(String userId, int clientId) throws ServiceException;

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
     * 判断username 或者ip是否在黑名单中
     *
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean isUserInBlackList(String username, String ip) throws ServiceException;

    /**
     * 检查用户名或者ip是否在白名单中
     *
     * @param username
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean checkLoginUserInWhiteList(String username, String ip) throws ServiceException;

    /**
     * 检查用户名或者ip是否需要被添加进黑名单中
     *
     * @param username
     * @param ip
     * @throws ServiceException
     */
    public boolean isLoginTimesForBlackList(String username, String ip) throws ServiceException;

    /**
     * 检查用户ip是否中黑名单
     *
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean isMobileSendSMSInBlackList(String ip) throws ServiceException;

    /**
     * 发短信次数
     *
     * @param ipOrMobile
     * @throws ServiceException
     */
    public void incSendTimesForMobile(final String ipOrMobile) throws ServiceException;

    /**
     * 找回密码计数
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean incFindPwdTimes(String passportId) throws ServiceException;

    /**
     * 找回密码计数
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean checkFindPwdTimes(String passportId) throws ServiceException;

    /**
     * 找回密码，重置密码次数限制--记录
     *
     * @param userId
     * @param clientId
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean incLimitFindPwdResetPwd(String userId, int clientId, String ip) throws ServiceException;

    /**
     * 找回密码，重置密码次数限制--检查是否超出限制
     *
     * @param userId
     * @param clientId
     * @param ip
     * @return
     * @throws ServiceException
     */
    public boolean isOverLimitFindPwdResetPwd(String userId, int clientId, String ip) throws ServiceException;

    public boolean isUserInExistBlackList(String username, String ip) throws ServiceException;

    public void incExistTimes(final String username, final String ip) throws ServiceException;


    /**
     * 内部接口检查用户是否存在次数累加
     *
     * @param username
     * @param ip
     * @throws ServiceException
     */
    public void incInterCheckUserTimes(final String username, final String ip) throws ServiceException;

    /**
     * 检查用户是否在GetPairtoken黑名单中
     */
    public boolean isUserInGetPairtokenBlackList(String username, String ip) throws ServiceException;

    /**
     * 记录GetPairtoken的次数
     *
     * @param username
     * @param ip
     * @throws ServiceException
     */
    public void incGetPairTokenTimes(final String username, final String ip) throws ServiceException;


    /**
     * 检查账号昵称是否存在是否中黑名单
     *
     * @param ip
     * @param cookie
     */
    public boolean checkNickNameExistInBlackList(final String ip, final String cookie);

    /**
     * 记录某IP检查账号昵称是否存在的次数
     *
     * @param ip
     * @param cookie
     */
    public void incCheckNickNameExistTimes(final String ip, final String cookie);
}
