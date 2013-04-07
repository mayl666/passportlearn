package com.sogou.upd.passport.dao.account;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-26
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */

import com.sogou.upd.passport.model.account.AccountAuth;

import java.util.List;
import java.util.Map;

/**
 * 用户状态表的接口mapper文件
 */
public interface AccountAuthMapper {
    /**
     * 往用户状态表中插入一条记录
     *
     * @param accountAuth
     * @return
     */
    public int insertAccountAuth(AccountAuth accountAuth);

    /**
     * 往用户状态表中插入一条记录
     *
     * @param accountAuth
     * @return
     */
    public int saveAccountAuth(AccountAuth accountAuth);

    /**
     * 更新用户状态表
     *
     * @param accountAuth
     * @return
     */
    public int updateAccountAuth(AccountAuth accountAuth);

    /**
     * 根据userId、clientId、instanceId三个条件获取AccountAuth信息
     *
     * @param
     * @return
     */
    public AccountAuth getAccountAuthByQuery(AccountAuth accountAuth);

    /**
     * 根据refresh_token获取AccountAuth信息
     * todo 可以和getUserAuthByUserId()合并，缓存怎么存？分表怎么查？
     *
     * @param
     * @return
     */
    public AccountAuth getAccountAuthByRefreshToken(String refreshToken);

    /**
     * 根据access_token获取AccountAuth信息
     * todo 可以和getUserAuthByUserId()合并，缓存怎么存？分表怎么查？
     *
     * @param
     * @return
     */
    public AccountAuth getAccountAuthByAccessToken(String accessToken);

    /**
     * 根据userid删除AccountAuth信息，内部调试接口使用
     * @param userId
     * @return
     */
    public void deleteAccountAuthByUserId(long userId);

    /**
     * 根据userId查询所有记录，返回list集合
     * @param accountAuth
     * @return
     */
    public List<AccountAuth> batchFindAccountAuthByUserId(AccountAuth accountAuth);

    /**
     * 批量更新某用户对应的状态记录
     * @param list
     */
    public void batchUpdateAccountAuth(List<AccountAuth> list);
}
