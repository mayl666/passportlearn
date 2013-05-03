package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.QQAPIValidator;
import net.sf.json.JSONException;

/**
 * QQ的用户类API响应结果
 * TODO 需增加相应的字段实现方法
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQUserAPIResponse extends OAuthClientResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new QQAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
//			parameters = JSONUtils.parseJSONObject(body);
        } catch (JSONException e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

    /*=================== 响应结果中的字段 ====================*/
    public String toUserInfo() {
//		UserProfile user = new UserProfile();
//		user.setNickname(formNickName(getParam(QQOAuth.NICK_NAME)));
//		user.setImageURL(getParam(QQOAuth.FIGURE_URL_2)); // TODO 转换为基础url
//		user.setGender(formGender(getParam(QQOAuth.GENDER)));
//		return user;
        return body;
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("男")) {
            sex = 1;
        }
        return sex;
    }

}
