package com.sogou.upd.passport.manager.moduleblacklist;

import com.sogou.upd.passport.service.moduleblacklist.ModuleBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-8-29
 * Time: 下午8:21
 */
@Component
public class ModuleBlackListManagerImpl implements ModuleBlackListManager {

    @Autowired
    private ModuleBlackListService moduleBlackListService;

    @Override
    public List<String> getBlackLists() {
        return moduleBlackListService.getBlackListForTest();
    }
}
