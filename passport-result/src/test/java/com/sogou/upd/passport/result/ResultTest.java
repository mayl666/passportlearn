package com.sogou.upd.passport.result;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.TestCase;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-25
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public class ResultTest extends TestCase {

    public void testGetMessage() {
        Result result = new APIResultSupport(false);
        result.setResultCode(ResultCode.SUCCESS);
        String messageID = result.getResultCode().getMap().getMessageID();
        String messageData = result.getResultCode().getMap().getMessageData();
        System.out.println("message id:" + messageID);
        System.out.println("message data:" + messageData);
        System.out.println("Fail Result Json:" + result);

        Result successResult = new APIResultSupport(true);
        successResult.setDefaultModel(newUser());
        successResult.setDefaultModel("addkey", "dfa4t632242");
        System.out.println("Success Result Json:" + successResult);


//        ClassLoader classLoader = ResultCode.class.getClassLoader();
//        ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
//        URL url = classLoader.getResource("\\com\\sogou\\upd\\passport\\result\\ResultCode.xml");
//        URL url1 = classLoader1.getResource("/com/sogou/upd/passport/ResultCode.xml");
//        System.out.println("url:" + url.toString());
    }

    private User newUser() {
        User user = new User();
        user.setName("spz");
        user.setAge(21);
        List list = Lists.newArrayList();
        list.add("aaaa");
        Map map = Maps.newHashMap();
        map.put("1", "dafa131");
        user.setList(list);
        user.setMap(map);
        return user;
    }
}
