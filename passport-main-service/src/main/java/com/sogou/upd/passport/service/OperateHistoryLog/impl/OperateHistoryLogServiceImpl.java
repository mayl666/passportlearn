package com.sogou.upd.passport.service.OperateHistoryLog.impl;

import com.sogou.upd.passport.dao.operatelog.OperateHistoryLogDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.operatelog.OperateHistoryLog;
import com.sogou.upd.passport.service.OperateHistoryLog.OperateHistoryLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 后台记录操作历史记录服务实现
 * User: chengang
 * Date: 14-8-8
 * Time: 下午6:10
 */
@Service
public class OperateHistoryLogServiceImpl implements OperateHistoryLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperateHistoryLogServiceImpl.class);


    @Autowired
    private OperateHistoryLogDAO operateHistoryLogDAO;

    @Override
    public boolean insertOperateHistoryLog(OperateHistoryLog operateHistoryLog) throws ServiceException {
        try {
            int insertResult = operateHistoryLogDAO.insertOperateHistoryLog(operateHistoryLog);
            if (insertResult != 0) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("insertOperateHistoryLog error.", e);
        }
        return false;

    }
}
