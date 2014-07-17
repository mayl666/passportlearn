package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午3:07
 */
@Ignore
public class ParseCookieUtilTest {
    @Test
    public void testParsePpinf() throws Exception {
        String ppinf = "2|1371541233|1372750833|bG9naW5pZDowOnx1c2VyaWQ6MTg6dXBkX3Rlc3RAc29nb3UuY29tfHNlcnZpY2V1c2U6MjA6MDAxMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjEwOjIwMTMtMDYtMDN8ZW10OjE6MHxhcHBpZDo0Ojk5OTl8dHJ1c3Q6MToxfHBhcnRuZXJpZDoxOjB8cmVsYXRpb246MDp8dXVpZDoxNjoyNmYxNWI1OGQwYzU0ZDVzfHVpZDoxNjoyNmYxNWI1OGQwYzU0ZDVzfHVuaXFuYW1lOjQ0OiVFNiU5MCU5QyVFNyU4QiU5MCVFNyVCRCU5MSVFNSU4RiU4QjU4NDMxMDc4fA";
        String[] ppinfs = ppinf.split("\\|");
        String userInfo = ppinfs[ppinfs.length - 1];
        String userInfoBase64 = Coder.decodeBASE64(userInfo);
        System.out.println(userInfoBase64);
        String[] userInfos = userInfoBase64.split("\\|");
        Map<String,String> maps=new HashMap(userInfos.length);
        for (String item : userInfos) {
            String[] items = item.split(":");
            if(items.length<3){
               continue;
            }
            String key=items[0];
            String value=items[2];
            maps.put(key,value);
        }
        System.out.println(maps);
        String nickname = maps.get(ParseCookieUtil.PPINF_UNIQNAME);
        nickname = URLDecoder.decode(nickname, CommonConstant.DEFAULT_CONTENT_CHARSET);
        System.out.println(nickname);
    }
}
