package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

/**
 * TODO 搜狗账号迁移后，搜狗账号昵称头像写入account表，其他账号写入account_base_info表
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 上午1:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountBaseInfoService {

    /**
     * 异步更新第三方个人资料
     * @param passportId
     * @param connectUserInfoVO
     */
    public void asyncUpdateAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO);

    /**
     * 存在则插入，不存在则更新AccountBaseInfo
     * 写AccountBaseInfo缓存和数据库
     * 写uniqname_passportid_mapping缓存和数据库
     * @param passportId
     * @param uniqname
     * @param avatar
     * @return
     */
    public boolean insertOrUpdateAccountBaseInfo(String passportId, String uniqname, String avatar);
}
