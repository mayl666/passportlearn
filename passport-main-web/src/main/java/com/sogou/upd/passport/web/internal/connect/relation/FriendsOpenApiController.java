package com.sogou.upd.passport.web.internal.connect.relation;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.FriendsOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.relation.FriendsOpenApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.OpenApiParams;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:30
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect/friends")
public class FriendsOpenApiController {

    @Autowired
    private FriendsOpenApiManager proxyFriendsOpenApiManager;

    /**
     * 获取第三方平台的用户好友列表和互相关注的列表
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/friendship/friends", method = RequestMethod.POST)
    @ResponseBody
    public Object getUserFriendship(FriendsOpenApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        BaseOpenApiParams baseOpenApiParams = new OpenApiParams().createBaseForm(params);
        result = proxyFriendsOpenApiManager.getUserFriends(baseOpenApiParams);
        return result.toString();
    }

}
