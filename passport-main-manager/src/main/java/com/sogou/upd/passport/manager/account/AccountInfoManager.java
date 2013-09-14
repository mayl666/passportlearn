package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

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

}
