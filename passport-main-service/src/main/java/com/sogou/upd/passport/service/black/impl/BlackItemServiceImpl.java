package com.sogou.upd.passport.service.black.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.black.BlackItemDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.black.BlackItem;
import com.sogou.upd.passport.service.black.BlackItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BlackItemServiceImpl implements BlackItemService{
    private static final Logger logger = LoggerFactory.getLogger(BlackItemServiceImpl.class);

    private static final Logger loginBlackListLogger = LoggerFactory.getLogger("com.sogou.upd.passport.loginBlackListFileAppender");
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private BlackItemDAO blackItemDAO;


    @Override
    public void addIPOrUsernameToLoginBlackList(String ipOrUsername, int reason,boolean isIp) throws ServiceException {
        try {
            String key = null;
            String timesKey = null;
            int sort = -1;
            String flag = "";
            if(isIp){
                key = buildLoginIPBlackKeyStr(ipOrUsername);
                timesKey = buildIPLoginTimesKeyStr(ipOrUsername);
                sort = BlackItem.BLACK_IP;
                flag ="ip";
            }else {
                key = buildLoginUserNameBlackKeyStr(ipOrUsername);
                timesKey = buildUserNameLoginTimesKeyStr(ipOrUsername);
                sort = BlackItem.BLACK_USERNAME;
                flag ="username";

            }
            redisUtils.setWithinSeconds(key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
            long expireSeconds = redisUtils.getExpireTime(timesKey);

            double durMin = 0;
            if(reason != BlackItem.Add_LIMIT){
                durMin = (double)(DateAndNumTimesConstant.TIME_ONEHOUR - expireSeconds)/60;
                DecimalFormat df = new DecimalFormat("#.00");
                durMin = Double.parseDouble(df.format(durMin));
            }

            //记入log
            String reasionStr ="ADD";
            if(reason == BlackItem.FAILED_LIMIT){
                reasionStr = "FAILED";
            }else if(reason == BlackItem.SUCCESS_LIMIT) {
                reasionStr = "SUCCESS";
            }
            StringBuilder log = new StringBuilder();
            Date date = new Date();
            log.append(new SimpleDateFormat("HH:mm:ss").format(date)).append(" ").append(flag)
                    .append(" ").append(ipOrUsername).append(" ").append(reasionStr).append(" ").append(durMin);
            loginBlackListLogger.info(log.toString());

            //记入数据
            String serverIP =  InetAddress.getLocalHost().getHostAddress();
            initialBlackItem(sort, ipOrUsername, reason, durMin, serverIP, BlackItem.SCOPE_LOGIN);
        } catch (Exception e) {
            logger.error("addIPToBlackList:" + ipOrUsername, e);
            throw new ServiceException(e);
        }
    }


    @Override
    public BlackItem initialBlackItem(int flagIp, String ipOrUsername,int flagSuccessLimit, Double durationTime, String insertServer,int scope) throws ServiceException {
        BlackItem blackItem = new BlackItem();
        try {
            blackItem.setNameSort(flagIp);
            blackItem.setName(ipOrUsername);
            blackItem.setLimitSort(flagSuccessLimit);
            blackItem.setDurationTime(durationTime);
            blackItem.setInsertTime(new Date());
            blackItem.setInsertServer(insertServer);
            blackItem.setScope(scope);
            long id = blackItemDAO.insertBlackItem(blackItem);
            if (id > 0) {
                return blackItem;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public List<BlackItem> getBlackItemList( Date startDate,Date endDate,Integer sort,
                                             String name,Integer flag_success_limit,
                                             Double minTime,Double maxTime,
                                             Integer scope,Integer start,Integer end) throws ServiceException{
        return blackItemDAO.getBlackItemList(startDate,endDate,sort,name,flag_success_limit,minTime,maxTime,scope,start,end);
    }

    @Override
    public int getBlackItemCount( Date startDate,Date endDate,Integer sort,
                                             String name,Integer flag_success_limit,
                                             Double minTime,Double maxTime,
                                             Integer scope,Integer start,Integer end) throws ServiceException{
        return blackItemDAO.getBlackItemCount(startDate,endDate,sort,name,flag_success_limit,minTime,maxTime,scope,start,end);
    }


    @Override
    public BlackItem getBlackItemByName(Date startDate,Date endDate,int sort,String name) throws ServiceException{
        return blackItemDAO.getBlackItemByName(startDate,endDate,sort,name);
    }

    @Override
    public int delBlackItem(long id) throws ServiceException{
        return blackItemDAO.delBlackItemById(id);
    }


    private static String buildLoginUserNameBlackKeyStr(String username) {
        return CacheConstant.CACHE_PREFIX_LOGIN_USERNAME_BLACK_ + username;
    }

    private static String buildLoginIPBlackKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_LOGIN_IP_BLACK_ + ip;
    }

    private static String buildUserNameLoginTimesKeyStr(String username) {
        return  CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
    }

    private static String buildIPLoginTimesKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
    }

}
