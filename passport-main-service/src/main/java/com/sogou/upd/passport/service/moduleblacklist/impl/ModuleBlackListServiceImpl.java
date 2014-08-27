package com.sogou.upd.passport.service.moduleblacklist.impl;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.moduleblacklist.ModuleBlacklist;
import com.sogou.upd.passport.service.moduleblacklist.ModuleBlackListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * nginx module 获取黑名单服务实现
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:45
 */
public class ModuleBlackListServiceImpl implements ModuleBlackListService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleBlackListServiceImpl.class);

    @Override
    public List<ModuleBlacklist> getWholeModuleBlackList() throws ServiceException {
        return null;
    }

    @Override
    public List<ModuleBlacklist> getIncreModuleBlackList(Date update_timestamp) throws ServiceException {
        return null;
    }
}
