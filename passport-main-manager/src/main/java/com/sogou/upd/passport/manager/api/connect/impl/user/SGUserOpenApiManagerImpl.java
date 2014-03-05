package com.sogou.upd.passport.manager.api.connect.impl.user;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:21
 * To change this template use File | Settings | File Templates.
 */
@Component("sgUserOpenApiManager")
public class SGUserOpenApiManagerImpl implements UserOpenApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGUserOpenApiManagerImpl.class);

    @Autowired
    private ConnectAuthService connectAuthService;

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = userOpenApiParams.getUserid();
            int original = userOpenApiParams.getOriginal();
            ConnectUserInfoVO cacheConnectUserInfoVO;
            int clientId = userOpenApiParams.getClient_id();
            if (original == CommonConstant.WITH_CONNECT_ORIGINAL) {
                //读第三方个人资料原始缓存
                cacheConnectUserInfoVO = connectAuthService.obtainConnectOriginalUserInfo(passportId, clientId);
            } else {
                //读第三方个人资料非原始缓存
                cacheConnectUserInfoVO = connectAuthService.obtainConnectUserInfo(passportId, clientId);
            }
            if (cacheConnectUserInfoVO == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                return result;
            }
            result = buildSuccResult(cacheConnectUserInfoVO, passportId, original);
            return result;

        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
        } catch (OAuthProblemException ope) {
            String errorCode = ope.getError();
            String errMsg = ErrorUtil.getERR_CODE_MSG(errorCode);
            if (StringUtils.isBlank(errMsg)) {
                logger.error("handle oauth authroize code error!", ope);
                result = buildErrorResult(errorCode, ope.getDescription());
            } else {
                result = buildErrorResult(errorCode, errMsg);
            }
        } catch (Exception exp) {
            logger.error("system error!", exp);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!");
        }
        return result;

    }

    private Result buildErrorResult(String errorCode, String errorText) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        return result;
    }

    private Result buildSuccResult(ConnectUserInfoVO connectUserInfoVO, String userid, int original) {
        Result userInfoResult = new APIResultSupport(true);
        Map<String, Object> data = Maps.newHashMap();
        Map<String, Object> result_value_data = Maps.newHashMap();
        result_value_data.put("id", AccountTypeEnum.getOpenIdByPassportId(userid));
        result_value_data.put("birthday", "");
        result_value_data.put("sex", connectUserInfoVO.getGender());
        result_value_data.put("nick", connectUserInfoVO.getNickname());
        if (original == CommonConstant.WITH_CONNECT_ORIGINAL) {
            String province = Strings.isNullOrEmpty(connectUserInfoVO.getProvince()) ? "" : connectUserInfoVO.getProvince();
            String city = Strings.isNullOrEmpty(connectUserInfoVO.getCity()) ? "" : " " + connectUserInfoVO.getCity();
            result_value_data.put("location", province + city);
        } else {
            result_value_data.put("location", "");
        }
        result_value_data.put("headurl", connectUserInfoVO.getAvatarLarge());
        data.put("result", result_value_data);
        if (original == CommonConstant.WITH_CONNECT_ORIGINAL) {
            data.put("original", CollectionUtils.isEmpty(connectUserInfoVO.getOriginal()) ? "" : connectUserInfoVO.getOriginal());
        }
        data.put("userid", userid);
        data.put("openid", userid);
        userInfoResult.setModels(data);
        userInfoResult.setMessage("操作成功");
        return userInfoResult;
    }

}