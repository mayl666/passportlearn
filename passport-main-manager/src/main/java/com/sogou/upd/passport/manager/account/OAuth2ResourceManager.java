package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public interface OAuth2ResourceManager {

    /**
     * 获取受保护的资源
     *
     * @param params
     * @return
     */
    public Result resource(PCOAuth2ResourceParams params);

    /**
     * 获取cookie值
     *
     * @return
     */
    public Result getCookieValue(String accessToken, int clientId, String clientSecret, String instanceId, String username);

    /**
     * 获取完整的个人信息
     *
     * @return
     */
    public Result getFullUserInfo(String accessToken, int clientId, String clientSecret, String instanceId, String username);

    /**
     * 通过token来获取passportId
     *
     * @param token
     * @param clientId
     * @param instanceId
     * @return
     */
    public Result queryPassportIdByAccessToken(String token, int clientId, String instanceId, String username);

    /**
     * 根据passportId获取昵称
     *
     * @param passportId
     * @return
     */
    public String getUniqname(String passportId, int clientId);

    /**
     * 获取urlencode之后的昵称
     *
     * @param passportId
     * @return
     */
    public String getEncodedUniqname(String passportId, int clientId);

    /**
     * 获取用户的头像和昵称
     *
     * @param passportId
     * @return
     */
    public Result getUniqNameAndAvatar(String passportId, int clientId);


    /**
     * 获取urlencode之后的昵称及其对应的头像
     *
     * @param passportId
     * @param clientId
     * @return
     */
    public String getEncodedUniqNameAndAvatar(String passportId, int clientId);

    /**
     * 浏览器PC/移动客户端登录
     *
     * @param oauthRequest
     * @return
     */
    public Result oauth2Authorize(OAuthTokenASRequest oauthRequest);

    /**
     * 通过token获取passportId
     * @param accessToken
     * @param clientId
     * @return
     */
    public Result getPassportIdByToken(String accessToken, int clientId);

}
