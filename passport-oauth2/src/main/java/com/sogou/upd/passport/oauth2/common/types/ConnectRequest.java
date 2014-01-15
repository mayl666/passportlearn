package com.sogou.upd.passport.oauth2.common.types;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;

/**
 * 判断第三方请求
 * User: mayan
 * Date: 13-12-4
 * Time: 下午2:37
 */
public class ConnectRequest {
    //判断是否是qq的wap接口
    public static boolean isQQWapRequest(String provider,String display){
        if(AccountTypeEnum.getProvider(provider)==AccountTypeEnum.QQ.getValue() &&
                ConnectDisplay.isSupportDisplay(display,provider) &&
                (display.equals(QQOAuth.WML_DISPLAY)||display.equals(QQOAuth.XHTML_DISPLAY))){
            return true;
        }else {
            return false;
        }
    }
}
