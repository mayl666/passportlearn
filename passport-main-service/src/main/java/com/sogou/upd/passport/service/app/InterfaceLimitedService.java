package com.sogou.upd.passport.service.app;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    初始化接口调用频次
     */
    public Map<Object,Object> initInterfaceTimes(int clientId, String url);
}
