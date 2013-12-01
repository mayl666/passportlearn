package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PCOAuth2LoginParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-27
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public interface PCOAuth2LoginManager {

    /**
     * 登陆接口
     *
     * @param parameters
     * @param ip
     * @param scheme
     * @return
     */
    public Result accountLogin(PCOAuth2LoginParams parameters, String ip, String scheme);

    /**
     * //兼容浏览器PC端sohu+接口
     *
     * @param clientId
     * @return
     */
    public int getClientId(int clientId);
}
