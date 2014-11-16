package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * 用于安全相关的代理
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午3:41
 */
public interface SecureApiManager {

    /**
     * 修改密码接口
     * @return
     */
    Result updatePwd(String passportId, int clientId, String oldPwd, String newPwd, String modifyIp);

    /**
     * 修改密保问题和答案
     * @param passportId
     * @param clientId
     * @param password
     * @param newQues
     * @param newAnswer
     * @param modifyIp
     * @return
     */
    Result updateQues(String passportId, int clientId, String password, String newQues, String newAnswer, String modifyIp);

}
