package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.*;

import java.util.Map;

/**
 * 用于安全相关的代理
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午3:41
 */
public interface SecureProxyManager {

    /**
     * 修改密码接口
     * @param updatePwdApiParams
     * @return
     */
    Map<String, Object> updatePwd(UpdatePwdApiParams updatePwdApiParams);

    /**
     * 修改密保问题和答案
     * @param updateQuesApiParams
     * @return
     */
    Map<String, Object> updateQues(UpdateQuesApiParams updateQuesApiParams);
}
