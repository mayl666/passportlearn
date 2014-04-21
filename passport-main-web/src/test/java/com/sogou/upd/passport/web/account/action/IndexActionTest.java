package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.web.JUnitActionBase;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午4:05
 */
@Ignore
public class IndexActionTest extends JUnitActionBase {

    @Test
    public void testLoginInterceptor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");

        Cookie ppinf=new Cookie("ppinf","2|1371542573|0|bG9naW5pZDowOnx1c2VyaWQ6MTg6dXBkX3Rlc3RAc29nb3UuY29tfHNlcnZpY2V1c2U6MjA6MDAxMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjEwOjIwMTMtMDYtMDN8ZW10OjE6MHxhcHBpZDo0OjEwMTl8dHJ1c3Q6MToxfHBhcnRuZXJpZDoxOjB8cmVsYXRpb246MDp8dXVpZDoxNjoyNmYxNWI1OGQwYzU0ZDVzfHVpZDoxNjoyNmYxNWI1OGQwYzU0ZDVzfHVuaXFuYW1lOjQ0OiVFNiU5MCU5QyVFNyU4QiU5MCVFNyVCRCU5MSVFNSU4RiU4QjU4NDMxMDc4fA");
        request.setRequestURI("/");
        request.setCookies(ppinf);
        request.addHeader(LoginConstant.USER_ID_HEADER,passportId);
        // 执行URI对应的action
        this.excuteAction(request, response);
        String result = response.getContentAsString();
        System.out.println(result);
        Assert.assertNotNull(result);
    }
}
