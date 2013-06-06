package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.RenrenAPIValidator;

/**
 * renren的用户类API响应结果
 * TODO 需增加相应的字段实现方法
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class RenrenUserAPIResponse extends OAuthClientResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new RenrenAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {

        this.body = body;
//		parameters = JSONUtils.parseIrregularJSONObject(body);

    }

	/*=================== 响应结果中的字段 ====================*/

    public String toUserInfo() {
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<RenrenUserInfoVO>>() {}.getType();
//        List<RenrenUserInfoVO> jsonList = gson.fromJson(this.body, type);
        return body;
    }

}
