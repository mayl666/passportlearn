package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.utils.BeanUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:16
 * To change this template use File | Settings | File Templates.
 */
public class MobileLog {

    public MobileLog(Map map) {
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            BeanUtil.setBeanProperty(this, key, String.valueOf(map.get(key)));
        }
    }

    public String toHiveString() {
        return this.toString();
    }
}
