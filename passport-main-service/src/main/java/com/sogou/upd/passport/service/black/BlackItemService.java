package com.sogou.upd.passport.service.black;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.black.BlackItem;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
public interface BlackItemService {

    /**
     * 将IP或者username插入黑名单
     * @param ipOrUsername
     * @param reason
     * @param isIp
     * @throws ServiceException
     */
    public void addIPOrUsernameToLoginBlackList(String ipOrUsername, int reason,boolean isIp) throws ServiceException;
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

    /**
     * 根据查询条件查询黑名单列表
     * @param startDate
     * @param endDate
     * @param sort
     * @param name
     * @param flag_success_limit
     * @param minTime
     * @param maxTime
     * @param scope
     * @param start
     * @param end
     * @return
     * @throws ServiceException
     */
    public List<BlackItem> getBlackItemList( Date startDate,Date endDate,Integer sort,
                                             String name,Integer flag_success_limit,
                                             Double minTime,Double maxTime,
                                             Integer scope,Integer start,Integer end) throws ServiceException;

    /**
     * 根据name获取当前黑名中的项
     * @param startDate
     * @param endDate
     * @param sort
     * @param name
     * @return
     * @throws ServiceException
     */
    public BlackItem getBlackItemByName(Date startDate,Date endDate,int sort,String name) throws ServiceException;

    /**
     * 根据id删除黑名单项
     * @param id
     * @return
     * @throws ServiceException
     */
    public int delBlackItem(long id) throws ServiceException;

    /**
     * 通过查询条件获取黑名单项列表数目
     * @param startDate
     * @param endDate
     * @param sort
     * @param name
     * @param flag_success_limit
     * @param minTime
     * @param maxTime
     * @param scope
     * @param start
     * @param end
     * @return
     * @throws ServiceException
     */
    public int getBlackItemCount( Date startDate,Date endDate,Integer sort,
                                  String name,Integer flag_success_limit,
                                  Double minTime,Double maxTime,
                                  Integer scope,Integer start,Integer end) throws ServiceException;
}
