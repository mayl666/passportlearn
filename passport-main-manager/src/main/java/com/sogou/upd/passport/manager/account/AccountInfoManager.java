package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
public interface AccountInfoManager {
    /**
     * 图片上传
     *
     * @param byteArr 需要上传图片流
     * @param passportId 用户ID
     * @param type 上传类别  0:本地图片上传 1:网络URL图片上传
     * @return Result格式的返回值，提示上传状态
     */
    public Result uploadImg(byte[] byteArr, String passportId, String type);

    /**
     * 图片上传
     *
     * @return Result格式的返回值，提示上传状态
     */
    public Result uploadDefaultImg(String webUrl,String clientId);
    //获取头像
    public Result obtainPhoto(String username, String size);

}
