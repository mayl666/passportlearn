package com.sogou.upd.passport.manager.api.connect.impl.user;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.ConnectTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
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
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = userOpenApiParams.getUserid();
            int original = userOpenApiParams.getOriginal();
            ConnectUserInfoVO connectUserInfoVO;
            int clientId = userOpenApiParams.getClient_id();
            if (original == CommonConstant.WITH_CONNECT_ORIGINAL) {
                //读第三方个人资料原始信息
                result = obtainConnectOriginalUserInfo(passportId, clientId);
            } else {
                //读第三方个人资料非原始信息
                result = obtainConnectUserInfo(passportId, clientId);
            }
            if (!result.isSuccess()) {
                return result;
            }
            connectUserInfoVO = (ConnectUserInfoVO) result.getModels().get("connectUserInfoVO");
            result = buildSuccResult(connectUserInfoVO, passportId, original);
            return result;

        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
        } catch (OAuthProblemException ope) {
            String errorCode = ope.getError();
            String errMsg = ErrorUtil.getERR_CODE_MSG(errorCode);
            if (StringUtils.isBlank(errMsg)) {
                logger.warn("handle oauth authroize code error!", ope);
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

    private Result obtainConnectOriginalUserInfo(String passportId, int clientId) throws ServiceException, IOException, OAuthProblemException {
        Result result = new APIResultSupport(false);
        ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainCachedConnectUserInfo(passportId);
        if (connectUserInfoVO == null) {
            result = sgConnectApiManager.obtainConnectToken(passportId, clientId);
            ConnectToken connectToken;
            if (result.isSuccess()) {
                connectToken = (ConnectToken) result.getModels().get("connectToken");
                int provider = AccountTypeEnum.getAccountType(passportId).getValue();
                String appKey = ConnectTypeEnum.getAppKey(provider);
                //读第三方api获取第三方用户信息,并更新搜狗DB的connect_token表
                connectUserInfoVO = connectAuthService.getConnectUserInfo(provider, appKey, connectToken);
                if (connectUserInfoVO != null) {
                    //原始信息写缓存
                    connectAuthService.initialOrUpdateConnectUserInfo(connectToken.getPassportId(), connectUserInfoVO);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                    return result;
                }
            } else {
                return result;
            }
        }
        result.setSuccess(true);
        result.setDefaultModel("connectUserInfoVO", connectUserInfoVO);
        return result;
    }

    public Result obtainConnectUserInfo(String passportId, int clientId) throws ServiceException, IOException, OAuthProblemException {
        Result result;
        int provider = AccountTypeEnum.getAccountType(passportId).getValue();
        String appKey = ConnectTypeEnum.getAppKey(provider);
        ConnectUserInfoVO connectUserInfoVO;
        ConnectToken connectToken;
        result = sgConnectApiManager.obtainConnectToken(passportId, clientId);
        if (result.isSuccess()) {
            connectToken = (ConnectToken) result.getModels().get("connectToken");
            connectUserInfoVO = buildConnectUserInfoVO(connectToken);
            if (connectUserInfoVO == null) { //创建vo不成功时（即头像、昵称、性别信息皆为空），读第三方api并更新搜狗DB的connect_token表
                connectUserInfoVO = connectAuthService.getConnectUserInfo(provider, appKey, connectToken);
                if (connectUserInfoVO != null) {
                    connectUserInfoVO.setOriginal(null); //屏蔽第三方原始信息
                    result.setSuccess(true);
                    result.setDefaultModel("connectUserInfoVO", connectUserInfoVO);
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                    return result;
                }
            } else {
                result.setSuccess(true);
                result.setDefaultModel("connectUserInfoVO", connectUserInfoVO);
                return result;
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private ConnectUserInfoVO buildConnectUserInfoVO(ConnectToken connectToken) {
        String nickname = connectToken.getConnectUniqname();
        String avatarSmall = connectToken.getAvatarSmall();
        String avatarMiddle = connectToken.getAvatarMiddle();
        String avatarLarge = connectToken.getAvatarLarge();
        String gender = connectToken.getGender();
        if (StringUtil.isNotEmpty(nickname, avatarSmall, avatarMiddle, avatarLarge, gender)) {
            ConnectUserInfoVO connectUserInfoVO = new ConnectUserInfoVO();
            connectUserInfoVO.setNickname(nickname);
            connectUserInfoVO.setAvatarSmall(avatarSmall);
            connectUserInfoVO.setAvatarMiddle(avatarMiddle);
            connectUserInfoVO.setAvatarLarge(avatarLarge);
            connectUserInfoVO.setGender(Integer.parseInt(gender));
            return connectUserInfoVO;
        }
        return null;
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