package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
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
     * @return Result格式的返回值，提示上传状态
     */
    public Result uploadImg(byte[] byteArr, String passportId, String type);

    /**
     * 图片上传
     *
     * @return Result格式的返回值，提示上传状态
     */
    public Result uploadDefaultImg(String webUrl, String clientId);

    //获取头像
    public Result obtainPhoto(String username, String size);

    //检查昵称是否重复
    public Result checkNickName(CheckNickNameParams params);

    //修改个人资料
    public Result updateUserInfo(AccountInfoParams infoParams, String ip);

    //获取个人资料
    public Result getUserInfo(ObtainAccountInfoParams params);


    /**
     * 非第三方数据迁移后，获取用户信息，用户昵称、头像 信息读取account_0~32、用户其他信息读 account_info_0~32
     *
     * @param params
     * @return
     */
    Result getUserInfoFromSGAfterDataMigration(ObtainAccountInfoParams params);


    /**
     * 非第三方数据迁移后，获取用户昵称信息采用此方法
     *
     * @param passportId
     * @param clientId
     * @return
     */
    public String getUserUniqName(String passportId, int clientId);


}
