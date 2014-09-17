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
     * 获取黑名单用于测试
     *
     * @return
     * @throws ServiceException
     */
    public List<String> getBlackListForTest() throws ServiceException;


    /**
     * 获取增量黑名单数据
     *
     * @param update_timestamp
     * @return
     * @throws ServiceException
     */
    public List<ModuleBlacklist> getIncreModuleBlackList(Date update_timestamp) throws ServiceException;

    /**
     * 查询userid是否存在与黑名单中
     *
     * @param userid
     * @return
     * @throws ServiceException
     */
    public boolean checkUseridExist(String userid) throws ServiceException;

    /**
     * 更新 黑名单user 有效期
     *
     * @param userid
     * @param expire_time
     * @return
     * @throws ServiceException
     */
    public boolean updateModuleBlackUserExpireTime(String userid, int expire_time) throws ServiceException;

    /**
     * 根据userid删除module 黑名单
     *
     * @param usrid
     * @return
     * @throws ServiceException
     */
    public boolean deleteModuleBlackListByUserid(String usrid) throws ServiceException;

    /**
     * 后台增加黑名单
     *
     * @param moduleBlacklist
     * @return
     * @throws ServiceException
     */
    public ModuleBlacklist insertModuleBlackList(ModuleBlacklist moduleBlacklist) throws ServiceException;


    /**
     * 后台批量添加黑名单userid 列表
     *
     * @param blackLists
     * @return
     * @throws ServiceException
     */
    public boolean batchInsertModuleBlackList(List<ModuleBlacklist> blackLists) throws ServiceException;

}
