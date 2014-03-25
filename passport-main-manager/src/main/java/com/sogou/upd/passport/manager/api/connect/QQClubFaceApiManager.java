package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.manager.api.connect.form.qq.QQClubFaceOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;

/**
 * User: Mayan
 * Date: 14-03-17
 * Time: 下午16:33
 * To change this template use File | Settings | File Templates.
 */
public interface QQClubFaceApiManager {
    /**
     * 调用
     * @param openId
     * @param openKey
     * @param qqParams
     * @return
     * @throws com.sogou.upd.passport.oauth2.common.utils.qqutils.OpensnsException
     */
    public String executeQQOpenApi(String openId, String openKey, QQClubFaceOpenApiParams qqParams) throws Exception;
}
