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
     * 此接口为注册成功用户生成token
     *
     * @param pcPairTokenParams
     * @return
     */
    public Result getPairToken(PcPairTokenParams pcPairTokenParams);

    /**
     * 用户正式注册
     *
     * @param params
     * @param ip
     * @return
     */
    public Result pcAccountRegister(PCOAuth2RegisterParams params, String ip);
}
