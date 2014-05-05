package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.connect.InfoOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.info.InfoOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.relation.FriendsOpenApiParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 第三方代理接口之消息类测试类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午8:39
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ProxyInfoOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private InfoOpenApiManager proxyInfoOpenApiManager;

    @Test
    public void testAddUserShareOrPic() {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid("2327267612@sina.sohu.com");
        baseOpenApiParams.setOpenid("2327267612@sina.sohu.com");
        InfoOpenApiParams infoOpenApiParams = new InfoOpenApiParams();
        infoOpenApiParams.setMessage("这是一条测试数据");
        String url = "http://download.ie.sogou.com/skin/thumb/theme/7c/53/d741949fd59381b08431d79e0868_b.jpg";
        String urlEncode = null;
        try {
            urlEncode = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        infoOpenApiParams.setUrl(urlEncode);
        Map map = this.convertObjectToMap(infoOpenApiParams);
        baseOpenApiParams.setParams(map);
        Result result = proxyInfoOpenApiManager.addUserShareOrPic(baseOpenApiParams);
        System.out.println(result.toString());
    }


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
        Result result = proxyInfoOpenApiManager.addUserShareOrPic(baseOpenApiParams);
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

    @Test
    public void testFastJson() throws IOException {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid("2327267612@sina.sohu.com");
        baseOpenApiParams.setOpenid("2327267612@sina.sohu.com");
        InfoOpenApiParams infoOpenApiParams = new InfoOpenApiParams();
        infoOpenApiParams.setMessage("这是一条测试数据");
        ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
        Map<String, Object> map1 = objectMapper.convertValue(infoOpenApiParams, Map.class);
        baseOpenApiParams.setParams(map1);
        //以下模拟，将java对象转化成Map，map转换成json..
        Map<String, Object> map2 = objectMapper.convertValue(baseOpenApiParams, Map.class);
        long s = System.currentTimeMillis();
        String string = objectMapper.writeValueAsString(map2);
        long e = System.currentTimeMillis();
        System.out.println(string);
    }

    @Test
    public void testConvertObjectToJSON() {
        InfoOpenApiParams infoOpenApiParams = new InfoOpenApiParams();
        infoOpenApiParams.setUserid("6060606060@sina.sohu.com");
        infoOpenApiParams.setOpenid("6060606060@sina.sohu.com");
        infoOpenApiParams.setMessage("这是一条测试数据");
        infoOpenApiParams.setUrl("http://download.ie.sogou.com/skin/thumb/theme/7c/53/d741949fd59381b08431d79e0868_b.jpg");
        ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
        Map map = objectMapper.convertValue(infoOpenApiParams, Map.class);
        map.remove("userid");
        map.remove(CommonConstant.CLIENT_ID);
        map.remove(CommonConstant.RESQUEST_CT);
        map.remove(CommonConstant.RESQUEST_CODE);
        map.remove("openid");
        map.remove("openapptype");
        String json = null;
        try {
            json = objectMapper.writeValueAsString(map);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(json);
    }

    @Test
    public void testCheckProvider() {
        String openid = "6060606060@sina.sohu.com";      //新浪
        String openid2 = "xxxxxx@qq.sohu.com";            //QQ空间
        String openid3 = "xxxxx@t.qq.sohu.com";           //腾讯微博
        String[] strings = openid2.split("\\.");
        String s1 = strings[0].substring(strings[0].indexOf("@") + 1, strings[0].length());
        //获取第三方类型
        String s2 = openid3.substring(openid3.indexOf("@") + 1, openid3.indexOf("sohu") - 1);
        System.out.println(s2);

    }
}
