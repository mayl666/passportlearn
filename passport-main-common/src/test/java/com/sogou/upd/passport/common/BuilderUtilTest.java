package com.sogou.upd.passport.common;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.BuilderUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-21
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class BuilderUtilTest extends TestCase {

    public void testMapAsString() {
        Map map = Maps.newHashMap();
        map.put("a", "1");
        map.put("b", "2");
        String str = BuilderUtil.mapAsString(map);
        System.out.println("Map AS String :" + str);
        Assert.assertTrue(true);
    }

}
