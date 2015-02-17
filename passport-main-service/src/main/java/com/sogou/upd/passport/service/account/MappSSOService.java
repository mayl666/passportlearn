package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.model.app.PackageNameSign;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-16
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
public interface MappSSOService {

    PackageNameSign baseSSOAppInfoCheck(int clientId, long ct, String decryptAppInfo);

    boolean checkSSOPackageSign(PackageNameSign packageNameSign);

    public String generateSSOToken(long ct, String packageName, String udid);

    public void saveSSOTokenToCache(String ssoToken);

    public String encryptSSOToken(String ssoToken, String clientSecret);

    public String generateTicket(int clientId, String udid, String ssoToken, String serverSecret);

}
