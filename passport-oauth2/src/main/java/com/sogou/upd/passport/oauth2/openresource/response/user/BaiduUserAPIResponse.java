package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.BaiduOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.BaiduAPIValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * renren的用户类API响应结果
 * TODO 需增加相应的字段实现方法
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class BaiduUserAPIResponse extends UserAPIResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new BaiduAPIValidator();
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
        ConnectUserInfoVO connectUserInfoVO = new ConnectUserInfoVO();
        connectUserInfoVO.setNickname(getParam(BaiduOAuth.NAME));
        connectUserInfoVO.setImageURL(getParam(BaiduOAuth.AVATAR));
        connectUserInfoVO.setGender(getGender());
        connectUserInfoVO.setProvince("");//百度不支持省 市信息
        connectUserInfoVO.setCity("");
        connectUserInfoVO.setOriginal(parameters);
        return connectUserInfoVO;
    }

    private int getGender() {
       String gender = getParam(BaiduOAuth.SEX);
        int sex = 0;
        if (gender.equals("男")) {
            sex = 1;
        }
        return sex;
    }


}
