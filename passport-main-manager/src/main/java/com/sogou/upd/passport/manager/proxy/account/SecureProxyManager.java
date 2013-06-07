package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.*;
import com.sogou.upd.passport.common.result.Result;
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
    Result updatePwd(UpdatePwdApiParams updatePwdApiParams);

    /**
     * 修改密保问题和答案
     * @param updateQuesApiParams
     * @return
     */
    Result updateQues(UpdateQuesApiParams updateQuesApiParams);
}
