package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;

/**
 * 用于处理用户数据相关的管理
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:15
 */
public interface UserInfoApiManager {

    /**
     * 用于获取用户基本信息的接口
     *
     * @param getUserInfoApiparams
     * @return
     */
    Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams);

    /**
     * @param updateUserInfoApiParams
     * @return
     */
    Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams);

    /**
     * 验证用户昵称是否唯一
     *
     * @param updateUserUniqnameApiParams
     * @return
     */
    Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams);


}
