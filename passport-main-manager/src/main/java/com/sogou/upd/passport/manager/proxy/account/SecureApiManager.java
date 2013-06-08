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
public interface SecureApiManager {

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

    /**
     * 获取用户安全信息接口
     * @param getUserSecureInfoApiParams
     * @return
     * sec_email 密保邮箱（绑定邮箱）
     * sec_mobile 密保手机（绑定手机）
     * sec_ques 密保问题
     */
    Result getUserSecureInfo(GetUserSecureInfoApiParams getUserSecureInfoApiParams);
}
