package com.sogou.upd.passport.web.internal.connect;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class OpenApiParamsHelper {
    /**
     * 根据子类构造符合参数规则的父类
     *
     * @param object
     * @return
     */
    public BaseOpenApiParams createBaseForm(Object object) {
        ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
        Map<String, Object> map = objectMapper.convertValue(object, Map.class);
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid(map.get("userid").toString());
        baseOpenApiParams.setOpenid(map.get("openid").toString());
        baseOpenApiParams.setClient_id(Integer.parseInt(map.get("client_id").toString()));
        baseOpenApiParams.setParams(convertObjectToMap(map));
        return baseOpenApiParams;
    }

    /**
     * 将第三方各类传进来的参数删除公共属性，只保留子类独有的属性
     *
     * @param map 各类对象，比如InfoOpenApiParams、FriendsOpenApiParams等这样的对象
     * @return
     */
    private Map convertObjectToMap(Map<String, Object> map) {
        map.remove("userid");
        map.remove(CommonConstant.CLIENT_ID);
        map.remove(CommonConstant.RESQUEST_CT);
        map.remove(CommonConstant.RESQUEST_CODE);
        map.remove("openid");
        map.remove("params");
        return map;
    }

}
