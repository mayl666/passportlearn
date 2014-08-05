package com.sogou.upd.passport.common.validator;

import com.sogou.upd.passport.common.validation.constraints.RuValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by denghua on 14-2-24.
 */
//@Ignore
public class RuValidatorTest {

    @Test
    public void testValid() {

        boolean result;
        RuValidator ru = new RuValidator();


        String url = "http://daohang.qq.com/";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://daohang.qq.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://www.sogou.com?aa=9";

        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://ie.account.sogou.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://ie.m.account.sogou.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://daohang.qq.com/jump.html?errorCode=20214&errorMsg=%E5%88%9B%E5%BB%BA%E7%94%A8%E6%88%B7%E5%A4%B1%E8%B4%A5";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://ie.sogou.com/jump.html?errorCode=20214&errorMsg=%E5%88%9B%E5%BB%BA%E7%94%A8%E6%88%B7%E5%A4%B1%E8%B4%A5";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://ie.account.sogou.com?clientid=10002";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);


        url = "http://www.qq.com_521_qq_diao_yu_wangzhan_789.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);

//        url="http://sogou.com";
//        result=ru.isValid(url,null);
//        assertEquals(true,result);

        url = "http://www.sina.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);


        url = "http://xxx.go2map.com";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://xxx.soso.com/";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://k.sogou.com:80/touch/";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://k.sogou.com:80";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://skin.qq.pinyin.cn/jump.html";
        result = ru.isValid(url, null);
        Assert.assertEquals(true, result);

        url = "http://sec.sogou.com/\"}}';alert();//&client_id=2013";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);

        url = "http://sec.sogou.comhttps://account.sogou.com/web/reg/mobile?ru=http://sec.sogou.comhttps://account.sogou.com/web/reg/email?ru=http://sec.sogou.com/\"}}';alert();//";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);

        url = "\"><script>alert(/Test_By_t00ls/)</script><img";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);

        url = "http://www.acunetix.tst";
        result = ru.isValid(url, null);
        Assert.assertEquals(false, result);

    }
}