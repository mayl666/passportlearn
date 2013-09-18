package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.api.account.form.UploadAvatarParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.PCOAuth2UpdateNickParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    private HostHolder hostHolder;

    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/userinfo/checknickname", method = RequestMethod.GET)
    @ResponseBody
    public Object checkNickName(HttpServletRequest request, @RequestParam(value = "nickname") String nickname) {
        Result result = new APIResultSupport(false);
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(nickname);
        updateUserUniqnameApiParams.setClient_id(SHPPUrlConstant.APP_ID);
        result = proxyUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        return result.toString();
    }

    @RequestMapping(value = "/userinfo/updatenickname", method = RequestMethod.POST)
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public Object updateNickName(HttpServletRequest request, PCOAuth2UpdateNickParams pcOAuth2UpdateNickParams) throws Exception {
        Result result = new APIResultSupport(false);
        if (hostHolder.isLogin()) {
            //参数验证
            String validateResult = ControllerHelper.validateParams(pcOAuth2UpdateNickParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String userId = hostHolder.getPassportId();

            UpdateUserInfoApiParams params = new UpdateUserInfoApiParams();
            params.setUserid(userId);
            params.setModifyip(getIp(request));
            params.setUniqname(pcOAuth2UpdateNickParams.getNick());
            result = proxyUserInfoApiManager.updateUserInfo(params);
            return result.toString();
        }
        return "redirect:/web/webLogin";
    }

    //头像上传
    @RequestMapping(value = "/userinfo/uploadavatar")
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public Object uploadAvatar(HttpServletRequest request, UploadAvatarParams params)
    {
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

            byte[] byteArr = multipartFile.getBytes();
            result = accountInfoManager.uploadImg(byteArr, userId,"0");

        }else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
        }
        return result.toString();
    }


    //默认头像上传
    @RequestMapping(value = "/userinfo/uploadefaultavatar")
    @ResponseBody
    public Object uploadDefaultAvatar(HttpServletRequest request, UploadAvatarParams params)
    {
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

        result=accountInfoManager.uploadDefaultImg(params.getImgurl(),String.valueOf(clientId));
        if(result.isSuccess()) {
            result=accountInfoManager.obtainPhoto(String.valueOf(clientId),size);
        }
        return result.toString();
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public String maxUploadSizeExceeded(){
        Result result = new APIResultSupport(false);
        result.setCode(ErrorUtil.ERR_CODE_PHOTO_TO_LARGE);
        return result.toString();
    }

}
