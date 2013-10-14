package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
public interface AccountInfoManager {
    //图片上传
    public Result uploadImg(byte[] byteArr, String passportId, String type);
    //获取头像
    public Result obtainPhoto(String username, String size);

    //检查昵称是否重复
    public Result checkNickName(CheckNickNameParams params);

    //修改个人资料
    public Result updateUserInfo(AccountInfoParams infoParams, String ip);

    //获取个人资料
    public Result getUserInfo(ObtainAccountInfoParams params);


}
