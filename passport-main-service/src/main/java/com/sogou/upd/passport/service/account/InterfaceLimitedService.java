package com.sogou.upd.passport.service.account;

/**
 * 接口频次调用
 * User: mayan
 * Date: 13-10-31
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public interface InterfaceLimitedService {
    /*
    初始化应用接口限制列表
     */
    public void initAppLimitedList(String cacheKey,String key,String limiTimes);
    /*
    获取限制次数，超限返回false
     */
    public boolean isObtainLimitedTimesSuccess(String key, int appId,String getTimes,String interfaceTimes);
}
