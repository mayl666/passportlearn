package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.SinaAPIValidator;
import net.sf.json.JSONException;

/**
 * sina微博的用户类API响应结果
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaUserAPIResponse extends OAuthClientResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new SinaAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
//            parameters = JSONUtils.parseJSONObject(body);
        } catch (JSONException e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

    /*=================== 响应结果中的字段 ====================*/
    public String toUserInfo() {
//        String provinceID = getParam(SinaOAuth.PROVINCE);
//        String cityID = getParam(SinaOAuth.CITY);
//        UserProfile user = new UserProfile();
//        user.setNickname(formNickName(getParam(SinaOAuth.SCREEN_NAME)));
//        user.setImageURL(getParam(SinaOAuth.AVATAR_LARGE));
//        user.setGender(formGender(getParam(SinaOAuth.GENDER)));
//        user.setUserDesc(getParam(SinaOAuth.DESC));
//        user.setProvince(formProvince(provinceID));
//        user.setCity(formCity(provinceID, cityID));
//        user.setRegion(getParam(SinaOAuth.LOCATION));
//        return user;
        return body;
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("m")) {
            sex = 1;
        }
        return sex;
    }

//    private String formProvince(String provinceID) {
//        return OpenAPIServiceImpl.sinaProvinceCache.get(provinceID);
//    }
//
//    private String formCity(String provinceID, String cityID) {
//        List<Map<String, String>> cityList = OpenAPIServiceImpl.sinaCityCache.get(provinceID);
//        String city = "未知";
//        for (Map<String, String> map : cityList) {
//            city = map.get(cityID);
//            if (!StringUtils.isEmpty(city)) break;
//        }
//
//        return city;
//    }

}
