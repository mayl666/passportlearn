package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;

/**
 * 桌面端注册流程Manager
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 下午7:06
 * To change this template use File | Settings | File Templates.
 */
public interface PCOAuth2RegManager {

    /**
     * 浏览器桌面端检查注册用户是否合法
     * @return
     */
    public Result isPcAccountNotExists(String username, boolean type);
}
