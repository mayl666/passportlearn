package com.sogou.upd.passport.service.black;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.black.BlackItem;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
public interface BlackItemService {

    /**
     * 插入一条黑名单项
     * @param flagIp
     * @param ipOrUsername
     * @param flagSuccessLimit
     * @param durationTime
     * @param insertServer
     * @param scope
     * @return
     * @throws ServiceException
     */
    public BlackItem initialBlackItem(int flagIp, String ipOrUsername,int flagSuccessLimit, Double durationTime, String insertServer,int scope) throws ServiceException;
}
