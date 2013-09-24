package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.RenrenAPIValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.util.List;
import java.util.Map;

/**
 * renren的用户类API响应结果
 * TODO 需增加相应的字段实现方法
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class RenrenUserAPIResponse extends UserAPIResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new RenrenAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            Map result = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
            parameters = (Map) result.get(RenrenOAuth.RESPONSE);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }

    }

	/*=================== 响应结果中的字段 ====================*/

    public ConnectUserInfoVO toUserInfo() {
        ConnectUserInfoVO connectUserInfoVO = new ConnectUserInfoVO();
        connectUserInfoVO.setNickname(getParam(RenrenOAuth.NAME));
        connectUserInfoVO.setImageURL(getImageURL());
        connectUserInfoVO.setGender(getGender());
        connectUserInfoVO.setProvince(getProvince());
        connectUserInfoVO.setCity(getCity());
        return connectUserInfoVO;
    }

    private String getImageURL() {
        List imageList = (List) this.parameters.get(RenrenOAuth.AVATAR);
        String url = "";
        if (imageList.size() > 0) {
            Map image = (Map) imageList.get(imageList.size() - 1);
            url = (String) image.get(RenrenOAuth.IMAGE_URL);
        }
        return url;
    }

    private int getGender() {
        Map basicInfo = (Map) this.parameters.get(RenrenOAuth.BASIC_INFORMATION);
        String sex = (String) basicInfo.get(RenrenOAuth.SEX);
        int gender = 0;
        if (sex.equals("MALE")) {
            gender = 1;
        }
        return gender;
    }

    private String getProvince() {
        Map basicInfo = (Map) this.parameters.get(RenrenOAuth.BASIC_INFORMATION);
        Map homeTown = (Map) basicInfo.get(RenrenOAuth.HOME_TOWN);
        return (String) homeTown.get(RenrenOAuth.PROVINCE);
    }

    private String getCity() {
        Map basicInfo = (Map) this.parameters.get(RenrenOAuth.BASIC_INFORMATION);
        Map homeTown = (Map) basicInfo.get(RenrenOAuth.HOME_TOWN);
        return (String) homeTown.get(RenrenOAuth.CITY);
    }

}
