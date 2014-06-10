package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public interface CommonManager {
    /**
     * 用户注册时ip次数的累加
     *
     * @param ip
     * @param uuidName
     */
    public void incRegTimes(String ip, String uuidName);

    /**
     * 内部接口注册的ip次数累加
     *
     * @param ip
     */
    public void incRegTimesForInternal(String ip,int client_id);

    /**
     * 检验code是否正确
     * @param firstStr
     * @param clientId
     * @param ct
     * @param originalCode
     * @return
     */
    public boolean isCodeRight(String firstStr,int clientId,long ct,String originalCode);
    /**
     * 根据字符串获取code值
     * @param firstStr
     * @param clientId
     * @param ct
     * @return
     */
    public String getCode(String firstStr, int clientId, long ct);

    /**
     * 应用是否有此API访问权限
     * 1.应用服务器在appconfig配置里的server_ip白名单里；
     * 2.APIName在appconfig配置里的scope里；
     * @param clientId
     * @param requestIp 服务器ip
     * @param apiName  如果此API有访问限制则传API名称，否则传null
     * @return
     */
    public boolean isAccessAccept(int clientId, String requestIp, String apiName);

}
