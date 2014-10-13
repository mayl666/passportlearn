package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 抽象接口的抽象类实现，封装具体策略用到的所有公用方法
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AbstractConnectProxyResultStrategy implements ConnectProxyResultStrategy {

    @Override
    public Result buildCommonResultByPlatform(HashMap<String, Object> paramMaps) {
        return null;
    }

    @Override
    public HashMap<String, Object> convertToFormatMap(HashMap<String, Object> map) {
        HashMap<String, Object> data = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            Set<Map.Entry<String, Object>> set = map.entrySet();
            if (!CollectionUtils.isEmpty(set)) {
                for (Map.Entry<String, Object> entry : set) {
                    data.put(entry.getKey().replace(entry.getKey().substring(0, 1), entry.getKey().substring(0, 1).toLowerCase()), entry.getValue());
                }
            }
        }
        return data;
    }
}
