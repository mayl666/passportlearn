package com.sogou.upd.passport.common.utils;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-9-6 Time: 上午10:19 To change this template use File | Settings | File Templates.
 */
public class ApiGroupUtilTest extends TestCase {

    @Test
    public void testGetGroup() {
        List<String> apis = new LinkedList<>();
        String api1 = "/web/login";
        String api2 = "/web/update";
        String api3 = "web/upd";
        String api4 = "web/upda/";
        String api5 = "/webupd";
        String api6 = "webupda/";
        String api7 = "webupdate";
        String api8 = null;
        apis.add(api1);
        apis.add(api2);
        apis.add(api3);
        apis.add(api4);
        apis.add(api5);
        apis.add(api6);
        apis.add(api7);
        apis.add(api8);

        Iterator<String> iterator = apis.iterator();
        while (iterator.hasNext()) {
            System.out.println(ApiGroupUtil.getApiGroup(iterator.next()));
        }

    }
}
