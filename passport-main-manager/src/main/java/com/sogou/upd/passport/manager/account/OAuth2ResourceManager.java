package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;

import javax.servlet.http.HttpServletResponse;

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
     * @param response
     * @param params
     * @return
     */
    public Result resource(HttpServletResponse response, PCOAuth2ResourceParams params);

    /**
     * 获取cookie值
     *
     * @return
     */
    public Result getCookieValue(HttpServletResponse response, String accessToken, int clientId, String clientSecret, String instanceId, String username);

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
    public Result queryPassportIdByAccessToken(String token,int clientId,String instanceId,String username);

    /**
     * 通过token获取passportId
     *
     * @param accessToken
     * @param clientId
     * @return
     */
    public Result getPassportIdByToken(String accessToken, int clientId);

}
