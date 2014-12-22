package com.sogou.upd.passport.util;

import com.sogou.upd.passport.common.math.Coder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-12-16
 * Time: 下午1:55
 */
public class CoderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoderTest.class);

    @Test
    public void testMd5() {
        try {
            Date date = new Date();
            String datetime = new SimpleDateFormat("yyyyMMddHHmm").format(date);
            String md5Date = Coder.encryptMD5(datetime);
            System.out.println(" md5Date " + md5Date + " and length: " + md5Date.length());
        } catch (Exception e) {
            LOGGER.error("testMd5 error.", e);
        }

    }


}
