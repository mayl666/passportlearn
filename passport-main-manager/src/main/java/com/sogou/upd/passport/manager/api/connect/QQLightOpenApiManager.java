package com.sogou.upd.passport.manager.api.connect;

import com.qq.open.OpensnsException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-28
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public interface QQLightOpenApiManager {
    /**
     * 调用
     * @param openId
     * @param openKey
     * @param qqParams
     * @return
     * @throws OpensnsException
     */
    public String executeQQOpenApi(String openId, String openKey, QQLightOpenApiParams qqParams) throws OpensnsException;
}
