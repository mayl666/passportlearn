package com.sogou.upd.passport.manager.moduleblacklist;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-8-29
 * Time: 下午8:20
 */
public interface ModuleBlackListManager {


    /**
     * 获取黑名单
     *
     * @return
     */
    public List<String> getBlackLists();


}
