package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
public interface AccountInfoManager {
    /**
     * 图片上传
     *
     * @param byteArr    需要上传图片流
     * @param passportId 用户ID
     * @param type       上传类别  0:本地图片上传 1:网络URL图片上传
     * @param ip         用户操作IP
     * @return Result格式的返回值，提示上传状态
     */
    public Result uploadImg(byte[] byteArr, String passportId, String type, String ip);

    //修改个人资料
    public Result updateUserInfo(AccountInfoParams infoParams, String ip);

    //获取个人资料
    public Result getUserInfo(ObtainAccountInfoParams params);

    /**
     * 非第三方数据迁移后，获取用户昵称信息采用此方法
     *
     * @param passportId
     * @param clientId
     * @return
     */
    public String getUserUniqName(String passportId, int clientId);

    /**
     * 获取用户昵称、头像信息
     *
     * @param infoApiparams
     * @return
     */
    public Result getUserNickNameAndAvatar(GetUserInfoApiparams infoApiparams);
}
