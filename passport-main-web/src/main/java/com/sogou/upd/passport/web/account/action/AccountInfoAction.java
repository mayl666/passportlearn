package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.api.account.form.UploadAvatarParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.CheckOrUpdateNickNameParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息相关 上传头像 修改昵称
 * User: mayan
 * Date: 13-8-7
 * Time: 下午2:18
 */
@Controller
@RequestMapping("/web")
public class AccountInfoAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoAction.class);

    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;


    @RequestMapping(value = "/userinfo/checknickname", method = RequestMethod.GET)
    @ResponseBody
    public Object checkNickName(HttpServletRequest request, CheckOrUpdateNickNameParams checkOrUpdateNickNameParams) {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(checkOrUpdateNickNameParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(checkOrUpdateNickNameParams.getNickname());
        updateUserUniqnameApiParams.setClient_id(SHPPUrlConstant.APP_ID);
        result = sgUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        return result.toString();
    }

    @RequestMapping(value = "/userinfo/updatenickname", method = RequestMethod.POST)
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public Object updateNickName(HttpServletRequest request, CheckOrUpdateNickNameParams checkOrUpdateNickNameParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(checkOrUpdateNickNameParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        if (!hostHolder.isLogin()) {
            return "redirect:/web/webLogin";
        }
        String userId = hostHolder.getPassportId();
        UpdateUserInfoApiParams params = new UpdateUserInfoApiParams();
        params.setUserid(userId);
        params.setModifyip(getIp(request));
        params.setUniqname(checkOrUpdateNickNameParams.getNickname());
        result = proxyUserInfoApiManager.updateUserInfo(params);
        return result.toString();

    }

    /**
     * 获取用户信息
     * <p/>
     * 数据迁移前（全量数据+增量数据完成导入前）:非第三方账号用户昵称、头像信息 读取account_base_info表，用户其他信息通过调用搜狐api获取
     * <p/>
     * 目标:数据迁移后（全量数据+增量数据完成导入后）:非第三方账号用户昵称、头像信息 读取account_0~32表，用户其他信息读取account_info_0~32表
     *
     * @param request
     * @param params
     * @param model
     * @return
     */
    @RequestMapping(value = "/userinfo/getuserinfo", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String obtainUserinfo(HttpServletRequest request, ObtainAccountInfoParams params, Model model) {
        Result result = new APIResultSupport(false);
        if (hostHolder.isLogin()) {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                model.addAttribute("data", result.toString());
                return "/person/index";
            }

            String userId = hostHolder.getPassportId();
            //验证client_id是否存在
            int clientId = Integer.parseInt(params.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                model.addAttribute("data", result.toString());
                return "/person/index";
            }

            if (Strings.isNullOrEmpty(params.getFields())) {
                params.setFields("province,city,gender,birthday,fullname,personalid");
            }

            params.setUsername(userId);
            //获取用户信息

            //TODO 待修改获取用户信息

            result = accountInfoManager.getUserInfo(params);
//            result.getModels().put("uniqname",(String)result.getModels().get("uniqname"));

            //TODO 修改此处取昵称 非第三方账号迁移后，统一调用 accountInfoManager 的 getUserUniqName 方法
//            result.getModels().put("uniqname", oAuth2ResourceManager.getEncodedUniqname(params.getUsername(), clientId));
            result.getModels().put("uniqname", accountInfoManager.getUserUniqName(params.getUsername(), clientId));


            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);
            if (result.isSuccess()) {
                if (domain == AccountDomainEnum.THIRD) {
                    result.setDefaultModel("disable", true);
                }
                model.addAttribute("data", result.toString());
                result.setMessage("获取个人信息成功");
                return "/person/index";
            }
        }
        return "redirect:/web/webLogin";
    }

    //设置或修改个人信息
    @RequestMapping(value = "/userinfo/update", method = RequestMethod.POST)
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public String updateUserInfo(HttpServletRequest request, AccountInfoParams infoParams) {
        Result result = new APIResultSupport(false);
        if (hostHolder.isLogin()) {

            //参数验证
            String validateResult = ControllerHelper.validateParams(infoParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id是否存在
            int clientId = Integer.parseInt(infoParams.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }

            String ip = getIp(request);
            String userId = hostHolder.getPassportId();

            infoParams.setUsername(userId);
            result = accountInfoManager.updateUserInfo(infoParams, ip);

        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
        }
        return result.toString();
    }

    //头像上传
    @RequestMapping(value = "/userinfo/uploadavatar")
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public Object uploadAvatar(HttpServletRequest request, UploadAvatarParams params) {
        Result result = new APIResultSupport(false);

        if (hostHolder.isLogin()) {

            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id是否存在
            int clientId = Integer.parseInt(params.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }

            String userId = hostHolder.getPassportId();

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("Filedata");

            //TODO 非第三方账号数据迁移 用户更新头像信息
            byte[] byteArr = multipartFile.getBytes();
            result = accountInfoManager.uploadImg(byteArr, userId, "0");

        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
        }
        return result.toString();
    }


    //默认头像上传
    @RequestMapping(value = "/userinfo/uploadefaultavatar")
    @ResponseBody
    public Object uploadDefaultAvatar(HttpServletRequest request, UploadAvatarParams params) {
        Result result = new APIResultSupport(false);

        //参数验证
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //验证client_id是否存在
        int clientId = Integer.parseInt(params.getClient_id());
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        String size = params.getImgsize();

        result = accountInfoManager.uploadDefaultImg(params.getImgurl(), String.valueOf(clientId));
        if (result.isSuccess()) {
            result = accountInfoManager.obtainPhoto(String.valueOf(clientId), size);
        }
        return result.toString();
    }


    //头像上传
    @RequestMapping(value = "/userinfo/avatarurl", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String uploadAvatarurl(HttpServletRequest request, Model model) throws Exception {
        Result result = new APIResultSupport(false);

        if (hostHolder.isLogin()) {

            String userId = hostHolder.getPassportId();

//            if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(userId)) ||AccountDomainEnum.PHONE.equals(AccountDomainEnum.getAccountDomain(userId))){
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
//                Result result1 = secureManager.queryAccountSecureInfo(userId, 1120, false);
//                result.setDefaultModel("uniqname",(String)result1.getModels().get("uniqname"));
//            }else {
//                result = secureManager.queryAccountSecureInfo(userId, 1120, false);
//            }


            result = secureManager.queryAccountSecureInfo(userId, 1120, false);

            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);
            if (domain == AccountDomainEnum.THIRD) {

                //非第三方账号迁移，获取用户昵称信息，统一调用 accountInfoManager 的 getUserUniqName方法
//                result.getModels().put("uniqname", oAuth2ResourceManager.getEncodedUniqname(userId, 1120));
                result.getModels().put("uniqname", accountInfoManager.getUserUniqName(userId, 1120));

                result.setDefaultModel("disable", true);
            }
            model.addAttribute("data", result.toString());
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
        }
        return "/person/avatar";
    }

}
