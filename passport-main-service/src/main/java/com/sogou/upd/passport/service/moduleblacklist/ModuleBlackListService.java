package com.sogou.upd.passport.service.moduleblacklist;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.moduleblacklist.ModuleBlacklist;

import java.util.Date;
import java.util.List;

/**
 * nginx module 获取黑名单数据服务
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:40
 */

public interface ModuleBlackListService {


    /**
     * 获取全量黑名单数据
     *
     * @return
     * @throws ServiceException
     */
    public List<ModuleBlacklist> getWholeModuleBlackList() throws ServiceException;


    /**
     * 获取增量黑名单数据
     *
     * @param update_timestamp
     * @return
     * @throws ServiceException
     */
    public List<ModuleBlacklist> getIncreModuleBlackList(Date update_timestamp) throws ServiceException;


}
