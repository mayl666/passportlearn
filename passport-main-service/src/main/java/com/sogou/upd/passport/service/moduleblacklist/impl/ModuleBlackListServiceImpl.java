package com.sogou.upd.passport.service.moduleblacklist.impl;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.moduleblacklist.ModuleBlackListDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.moduleblacklist.ModuleBlacklist;
import com.sogou.upd.passport.service.moduleblacklist.ModuleBlackListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * nginx module 获取黑名单服务实现
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:45
 */
@Service
public class ModuleBlackListServiceImpl implements ModuleBlackListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleBlackListServiceImpl.class);

    private static final String CACHE_KEY_MODULE_BLACKLIST = "nginx_module_blacklist";

    private static final String CACHE_KEY_MODULE_BLACKLIST_VALUE_PREFIX = "module_blacklist_test";

    private static final int BLACK_LIST_SIZE = 10000;


    @Autowired
    private ModuleBlackListDAO moduleBlackListDAO;

    @Autowired
    private RedisUtils redisUtils;


    //添加module 黑名单，只对指定IP、user 白名单进行开放。


    @Override
    public List<ModuleBlacklist> getWholeModuleBlackList() throws ServiceException {
        return null;
    }

    @Override
    public List<String> getBlackListForTest() throws ServiceException {
        List<String> blackLists = redisUtils.getList(CACHE_KEY_MODULE_BLACKLIST);
        if (blackLists == null || blackLists.isEmpty()) {
            for (int i = 0; i < BLACK_LIST_SIZE; i++) {
                redisUtils.lPush(CACHE_KEY_MODULE_BLACKLIST, CACHE_KEY_MODULE_BLACKLIST_VALUE_PREFIX + i + "@sogou.com");
            }
        }
        return blackLists;
    }

    @Override
    public List<ModuleBlacklist> getIncreModuleBlackList(Date update_timestamp) throws ServiceException {
        return null;
    }

    @Override
    public boolean checkUseridExist(String userid) throws ServiceException {


        return false;
    }

    @Override
    public boolean updateModuleBlackUserExpireTime(String userid, int expire_time) throws ServiceException {
        return false;
    }

    @Override
    public boolean deleteModuleBlackListByUserid(String usrid) throws ServiceException {
        return false;
    }

    @Override
    public ModuleBlacklist insertModuleBlackList(ModuleBlacklist moduleBlacklist) throws ServiceException {
        return null;
    }

    @Override
    public boolean batchInsertModuleBlackList(List<ModuleBlacklist> blackLists) throws ServiceException {
        return false;
    }
}
