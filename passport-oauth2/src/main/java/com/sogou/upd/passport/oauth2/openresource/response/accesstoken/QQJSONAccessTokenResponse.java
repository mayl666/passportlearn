package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.util.Map;

/**
 * QQ.web正确返回结果为text/html
 * 异常结果为(callback：{json})
 * QQ.wap正常和异常返回结果均为text/html
 * 响应码为200
 * 变态！变态！变态！变态！
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            try {
                this.parameters = OAuthUtils.parseQQIrregularJSONObject(this.body);
            } catch (Exception e1) {
                try {
                   this.parameters = OAuthUtils.parseQQIrregularStringObject(this.body);
                } catch (Exception e2) {
                    throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                            "Invalid response! Response body resolve error,body is " + this.body);
                }

            }
        }
    }

    /**
     * QQ Authoz Code不返回openid，需额外接口调用
     *
     * @return
     */
    @Override
    public String getOpenid() {
        String value = getParam(OAuth.OAUTH_OPENID);
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    public ConnectUserInfoVO getUserInfo() {
        ConnectUserInfoVO connectUserInfoVO = null;
        Object value = parameters.get(QQOAuth.USER_INFO);
        if (value != null && value instanceof Map) {
            Map userInfoMap = (Map) value;
            connectUserInfoVO = new ConnectUserInfoVO();
            connectUserInfoVO.setNickname((String) userInfoMap.get(QQOAuth.NICK_NAME));
            connectUserInfoVO.setGender(formGender((String) userInfoMap.get(QQOAuth.GENDER)));
            connectUserInfoVO.setAvatarSmall((String) userInfoMap.get(QQOAuth.FIGURE_URL_40));
            connectUserInfoVO.setAvatarMiddle((String) userInfoMap.get(QQOAuth.FIGURE_URL_100));
            connectUserInfoVO.setAvatarLarge((String) userInfoMap.get(QQOAuth.FIGURE_URL_100));
        }
        return connectUserInfoVO;
    }

    /**
     * QQ Authoz Code不返回nickName
     *
     * @return ""
     */
    @Override
    public String getNickName() {
        return "";
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("male")) {
            sex = 1;
        }
        return sex;
    }

}
