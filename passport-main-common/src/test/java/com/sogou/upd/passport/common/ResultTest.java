package com.sogou.upd.passport.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-25
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ResultTest extends TestCase {

    public void testGetMessage() {
        Result result1 = new APIResultSupport(false);  // 设置false
        result1.setCode(ErrorUtil.ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED);
        System.out.println("Fail Result Json:" + result1);

        Result result2 = new APIResultSupport(false);  // 设置false
        result2.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
        result2.setMessage("账号密码错误");
        System.out.println("Fail Result Json:" + result2);

        Result result3 = new APIResultSupport(false);
        result3.setSuccess(true);
        result3.setMessage("登录成功");
        System.out.println("Success Result Json:" + result3);

        Result result4 = new APIResultSupport(false);
        result4.setSuccess(true);
        result4.setMessage("登录成功");
        result4.setDefaultModel("openid","daf23413121");
        System.out.println("Success Result Json:" + result4);

        Result result5 = new APIResultSupport(true);
        result5.setDefaultModel(newUser());
        result5.setDefaultModel("addkey", "dfa4t632242");
        System.out.println("Success Result Json:" + result5);

        Result result6 = new APIResultSupport(true);
        result6.setModels(newMap());
        System.out.println("Success Result Json:" + result6);

        Result result7 = new APIResultSupport(true);
        result7.setModels(newMap());
        result7.setDefaultModel("addkey", "dfa4t632242");
        System.out.println("Success Result Json:" + result7);

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

    private Map newMap() {
        Map<String, Object> mapResult = Maps.newHashMap();
        mapResult.put("access_token", "dasfaeqwffa");
        mapResult.put("expires_time", "13326764745");
        mapResult.put("refresh_token", "dfaafasdfa");
        return mapResult;
    }
}
