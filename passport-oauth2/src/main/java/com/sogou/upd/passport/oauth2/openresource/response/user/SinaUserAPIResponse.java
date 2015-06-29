package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.SinaOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.SinaAPIValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sina微博的用户类API响应结果
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaUserAPIResponse extends UserAPIResponse {

    // 省份map，{11=北京}
    private static ConcurrentHashMap<Integer, String> sinaProvinceCache = new ConcurrentHashMap<Integer, String>();
    // 城市map，{11={1=海淀}}
    private static ConcurrentHashMap<Integer, List<Map<String, String>>> sinaCityCache = new ConcurrentHashMap<Integer, List<Map<String, String>>>();

    static {
        obtainSinaProvinces();
    }

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new SinaAPIValidator();
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
        Integer provinceID = Integer.parseInt(getParam(SinaOAuth.PROVINCE));
        String cityID = getParam(SinaOAuth.CITY);
        ConnectUserInfoVO user = new ConnectUserInfoVO();
        user.setNickname(getParam(SinaOAuth.SCREEN_NAME));
        user.setAvatarSmall(getParam(SinaOAuth.PROFILE_IMAGE_URL));   // 50*50
        user.setAvatarMiddle(getParam(SinaOAuth.PROFILE_IMAGE_URL));   // 50*50
        user.setAvatarLarge(getParam(SinaOAuth.AVATAR_LARGE));         // 180*180
        user.setGender(formGender(getParam(SinaOAuth.GENDER)));
        user.setUserDesc(getParam(SinaOAuth.DESC));
        user.setProvince(sinaProvinceCache.get(provinceID));
        user.setCity(formCity(provinceID, cityID));
        user.setRegion(getParam(SinaOAuth.LOCATION));
        user.setOriginal(parameters);
        return user;
    }

    private int formGender(String gender) {
        int sex = 0;
        if (gender.equals("m")) {
            sex = 1;
        }
        return sex;
    }

    private String formCity(Integer provinceID, String cityID) {
        List<Map<String, String>> cityList = sinaCityCache.get(provinceID);
        String city = "未知";
        for (Map<String, String> map : cityList) {
            city = map.get(cityID);
            if (!Strings.isNullOrEmpty(city)) {
                city = StringUtil.exchangeToUf8(city);
                break;
            }
        }
        return city;
    }

    private static void obtainSinaProvinces() {
        try {
            String url = SinaOAuth.SINA_PROVINCES_FORMAT_URL;
            RequestModel requestModel = new RequestModel(url);
            Map provincesMap = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            List<Map> provincesList = (List<Map>) provincesMap.get("provinces");
            // 省份map，{11=北京}
            for (Map province : provincesList) {
                Integer id = (Integer) province.get("id");
                String name = (String) province.get("name");
                name = StringUtil.exchangeToUf8(name);
                sinaProvinceCache.putIfAbsent(id, name);
                // 城市map，{11={1=海淀}}
                List<Map<String, String>> cityList = (List<Map<String, String>>) province.get("citys");
                sinaCityCache.putIfAbsent(id, cityList);
            }
        }catch (Exception e){
        }
    }

}
