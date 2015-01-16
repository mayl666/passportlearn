package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.utils.BeanUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 移动端基础log对象，包含cinfo信息
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:16
 * To change this template use File | Settings | File Templates.
 */
public class MobileBaseLog {

    public MobileBaseLog(Map map) {
        if (!MapUtils.isEmpty(map)) {
            Set keys = map.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                BeanUtil.setBeanProperty(this, key, String.valueOf(map.get(key)));
            }
        }
    }

    public String toHiveString() {
        return this.toString();
    }
}
