package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.WeiXinOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.WeiXinAPIValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-20
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class WeiXinUserAPIResponse extends UserAPIResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new WeiXinAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

    /*=================== 响应结果中的字段 ====================*/
    public ConnectUserInfoVO toUserInfo() {
        ConnectUserInfoVO user = new ConnectUserInfoVO();
        user.setNickname(getParam(WeiXinOAuth.NICK_NAME));
        String headimgurl = getParam(WeiXinOAuth.HEADIMGURL);
        if (!Strings.isNullOrEmpty(headimgurl)) {
            String imgurl = headimgurl.substring(0, headimgurl.lastIndexOf("/"));
            user.setAvatarSmall(imgurl + "/" + WeiXinOAuth.TINY_46);    // 46*46
            user.setAvatarMiddle(imgurl + "/" + WeiXinOAuth.MIDDLE_96);  // 96*96
            user.setAvatarLarge(imgurl + "/" + WeiXinOAuth.LARGE_0);   // 640*640
        }
        user.setGender(formGender(getParam(WeiXinOAuth.SEX)));
        user.setCountry(getParam(WeiXinOAuth.COUNTRY));
        user.setProvince(getParam(WeiXinOAuth.PROVINCE));
        user.setCity(getParam(WeiXinOAuth.CITY));
        user.setPrivilege(getParam(WeiXinOAuth.PROVILEGE));
        user.setUnionid(getParam(WeiXinOAuth.UNIONID));
        user.setOriginal(parameters);
        return user;
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("男")) {
            sex = 1;
        }
        return sex;
    }
}
