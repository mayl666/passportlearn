package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.parameter.ConnectTransformat;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.impl.qq.MailConnectProxyResultStrategy;
import com.sogou.upd.passport.manager.api.connect.impl.qq.QzoneConnectProxyResultStrategy;
import com.sogou.upd.passport.manager.api.connect.impl.qq.WeiboConnectProxyResultStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConnectResultContext {

    private ConnectProxyResultStrategy connectProxyResultStrategy;

    public ConnectResultContext() {
    }

    public Result getResultByPlatform(String platformStr, HashMap<String, Object> maps) {
        Result result;
        ConnectTransformat platform = (ConnectTransformat) ConnectTransformat.getConnectPlatform(platformStr);
        switch (platform) {
            case mail:
                this.connectProxyResultStrategy = new MailConnectProxyResultStrategy();
                break;
            case weibo:
                this.connectProxyResultStrategy = new WeiboConnectProxyResultStrategy();
                break;
            case qzone:
                this.connectProxyResultStrategy = new QzoneConnectProxyResultStrategy();
                break;
        }
        result = connectProxyResultStrategy.buildCommonResultByPlatform(maps);
        return result;
    }
}
