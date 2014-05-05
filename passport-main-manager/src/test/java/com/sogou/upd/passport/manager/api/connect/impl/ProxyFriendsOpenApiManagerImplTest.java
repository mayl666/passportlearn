package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.connect.FriendsOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.relation.FriendsOpenApiParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 第三方代理接口之关系类测试类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午8:43
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ProxyFriendsOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private FriendsOpenApiManager proxyFriendsOpenApiManager;


    @Test
    public void testGetUserFriends() {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid("2327267612@sina.sohu.com");
        baseOpenApiParams.setOpenid("2327267612@sina.sohu.com");
        FriendsOpenApiParams friendsOpenApiParams = new FriendsOpenApiParams();
        friendsOpenApiParams.setPage(new Integer(1));
        friendsOpenApiParams.setCount(new Integer(1));
        Map map = this.convertObjectToMap(friendsOpenApiParams);
        baseOpenApiParams.setParams(map);
        Result result = proxyFriendsOpenApiManager.getUserFriends(baseOpenApiParams);
        System.out.println(result.toString());
    }

    private Map convertObjectToMap(Object object) {
        ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
        Map map = objectMapper.convertValue(object, Map.class);
        map.remove("userid");
        map.remove(CommonConstant.CLIENT_ID);
        map.remove(CommonConstant.RESQUEST_CT);
        map.remove(CommonConstant.RESQUEST_CODE);
        map.remove("openid");
        map.remove("openapptype");
        map.remove("params");
        return map;
    }
}
