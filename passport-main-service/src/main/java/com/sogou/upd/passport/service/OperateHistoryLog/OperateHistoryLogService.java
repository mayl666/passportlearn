package com.sogou.upd.passport.service.OperateHistoryLog;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.operatelog.OperateHistoryLog;

/**
 * 后台记录操作历史记录服务
 * User: chengang
 * Date: 14-8-8
 * Time: 下午6:07
 */

public interface OperateHistoryLogService {


    /**
     * 后台操作历史记录
     *
     * @param operateHistoryLog
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     */
    public boolean insertOperateHistoryLog(OperateHistoryLog operateHistoryLog) throws ServiceException;
}
