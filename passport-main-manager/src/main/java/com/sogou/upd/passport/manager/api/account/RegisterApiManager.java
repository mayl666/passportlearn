package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileApiParams;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:09
 * To change this template use File | Settings | File Templates.
 */
public interface RegisterApiManager {

    /**
     * 注册邮箱、个性域名（昵称+@Sogou）账号
     * 外域邮箱发送激活邮件，其他账号直接注册
     *
     * @param regEmailApiParams
     * @return
     */
    public Result regMailUser(RegEmailApiParams regEmailApiParams);

    /**
     * 注册手机账号，发给用户的验证码。
     * TODO 在SG流程里可以直接调用SG实现类，无需先调用Proxy，以后改SG
     *
     * @return
     */
    public Result sendMobileRegCaptcha(int clientId, String mobile);

    /**
     * 检查用户名是否已经被注册
     * userid可以为xxx@sogou.com、xxx@sohu.com、xxx@126.com、xxx@qq.sohu.com
     * 当acceptSohuDomain=false时，不允许为13621009174@sohu.com，如果是手机账号需要调用wapgetuserid接口
     *当acceptSohuDomain=true时，允许搜狐账号，该值为true主要是由于搜狐账号导入passport，便于找回密码操作
     * @return
     */
    public Result checkUser(String username, int clientId,boolean acceptSohuDomain);

    /**
     * 手机号直接注册，不经验证码——地图专用接口
     *
     * @param regMobileApiParams
     * @return
     */
    public Result regMobileUser(RegMobileApiParams regMobileApiParams);

    /**
     * 检查账号是否已经注册，与checkUser有区别，checkUser如果accountFlag!=1,认为账号不存在
     *
     * @param username
     * @param clientId
     * @return
     */
    public Result checkAccountExist(String username, int clientId);

    /**
     * 检查用户是搜狗输入法泄露账号
     */
    public boolean isSogouLeakList(String username, Account account);
}
