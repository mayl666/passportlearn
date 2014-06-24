package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.web.JUnitActionBase;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-17
 * Time: 下午2:07
 */
@Ignore
public class UserInfoApiControllerTest extends JUnitActionBase {



    @Test
    public void testUpdateUserInfo() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("POST");


        request.setRequestURI("/internal/account/updateuserinfo");
        request.addParameter("userid", passportId);
        request.addParameter("birthday", "2000-01-02");
        request.addParameter("gender", "2");
        request.addParameter("province", "110000");
        request.addParameter("city", "110100");
        request.addParameter("modifyip", modifyIp);
        // 执行URI对应的action
        this.excuteAction(request, response);
        String result = response.getContentAsString();
        System.out.println(result);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetUserInfo() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/internal/account/userinfo");
        request.addParameter("fields", "usertype,createip,birthday,gender,createip,createtime,personalid,personalidflag,sec_mobile,sec_email,province,city,createtime,sec_ques,avatarurl,regappid");
        request.addParameter("userid", "18612987312");
        request.setMethod("POST");

        // 执行URI对应的action
        this.excuteAction(request, response);
        String result = response.getContentAsString();
        System.out.println(result);
        Assert.assertNotNull(result);
    }
}
