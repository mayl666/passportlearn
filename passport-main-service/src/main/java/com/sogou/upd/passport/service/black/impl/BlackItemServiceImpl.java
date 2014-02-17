package com.sogou.upd.passport.service.black.impl;

import com.sogou.upd.passport.dao.black.BlackItemDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.black.BlackItem;
import com.sogou.upd.passport.service.black.BlackItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BlackItemServiceImpl implements BlackItemService{

    @Autowired
    private BlackItemDAO blackItemDAO;

    @Override
    public BlackItem initialBlackItem(int flagIp, String ipOrUsername,int flagSuccessLimit, Double durationTime, String insertServer) throws ServiceException {
        BlackItem blackItem = new BlackItem();
        try {
            blackItem.setFlagIp(flagIp);
            blackItem.setIpOrUsername(ipOrUsername);
            blackItem.setFlagSuccessLimit(flagSuccessLimit);
            blackItem.setDurationTime(durationTime);
            blackItem.setInsertTime(new Date());
            blackItem.setInsertServer(insertServer);
            long id = blackItemDAO.insertBlackItem(blackItem);
            if (id > 0) {
                return blackItem;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }
}
