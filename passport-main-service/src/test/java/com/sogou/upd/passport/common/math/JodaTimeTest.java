package com.sogou.upd.passport.common.math;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class JodaTimeTest {

    @Test
    public void testPlusMethod() {
        int expiresIn = 3600 * 24;

        DateTime dateTime = new DateTime();
        DateTime date1 = dateTime.plusSeconds(expiresIn);
        long time1 = date1.getMillis();

        Assert.assertEquals(time1, dateTime.getMillis()+expiresIn*1000);
    }

}
