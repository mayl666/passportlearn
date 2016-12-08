package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.BaseUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.ModuleBlackListParams;
import com.sogou.upd.passport.manager.api.account.form.SendSmsApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdatePswParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import static com.sogou.upd.passport.common.parameter.AccountDomainEnum.THIRD;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/security")
public class SecureApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SecureApiController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private RegisterApiManager registerApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private ResetPwdManager resetPwdManager;

    @Autowired
    private RedisUtils redisUtils;

    //黑名单用户列表分隔符
    private static final String BLACK_USER_LIST_VALUE_SPLIT = "\r\n";

    //返回给module结果集中userid与时间戳的分隔符
    private static final String BLACK_USER_EXPIRETIME_SPLIT = " ";

    //redis中保存黑名单userid与时间戳分隔符
    private static final String BLACK_USER_EXPIRETIME_REDIS_SPLIT = "\\^";

    //有效期
    private static final int EXPIRE_TIME = 60;

    /**
     * 手机发送短信重置密码
     */
    @RequestMapping(value = "/resetpwd_batch", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String resetpwd(HttpServletRequest request, BaseResetPwdApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String lists = params.getLists();
        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, request)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            if (!Strings.isNullOrEmpty(lists)) {
                List<UserNamePwdMappingParams> list = new ObjectMapper().readValue(lists, new TypeReference<List<UserNamePwdMappingParams>>() {
                });
                result = secureManager.resetPwd(list, clientId);
            } else {
                result.setSuccess(true);
                result.setMessage("lists为空");
            }
            return result.toString();
        } catch (Exception e) {
            log.error("Batch resetpwd fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("lists", lists);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    @RequestMapping(value = "/sendsms", method = RequestMethod.GET)
    @ResponseBody
    @InterfaceSecurity
    public Object sendSmsNewMobile(HttpServletRequest request, SendSmsApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String sgid = params.getSgid();
        String mobile = params.getMobile();
        String ip = params.getCreateip();
        String passportId = "UNKNOWN";
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            result = sessionServerManager.getPassportIdBySgid(sgid, ip);
            if (!result.isSuccess()) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }

            passportId = (String)result.getModels().get("passport_id");

            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (domain == AccountDomainEnum.PHONE) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            }

            if (domain == THIRD) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
            }
            //双读，检查新手机是否允许绑定
            result = registerApiManager.checkUser(mobile, clientId, false);
            if (!result.isSuccess()) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("手机号已绑定其他账号");
                return result.toString();
            }
            result = secureManager.sendMobileCode(mobile, clientId, AccountModuleEnum.SECURE);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("mobile", mobile);
            userOperationLog.putOtherMessage("sgid", sgid);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /**
     * 绑定密保手机
     */
    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String bindmobile(HttpServletRequest request, BindMobileApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String sgid = params.getSgid();
        String mobile = params.getMobile();
        String ip = params.getCreateip();
        String passportId = "UNKNOWN";
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            result = sessionServerManager.getPassportIdBySgid(sgid, ip);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }

            passportId = (String)result.getModels().get("passport_id");

            result = secureManager.bindMobileByPassportId(passportId, clientId, mobile, params.getSmscode(), params.getPassword(), false, ip);
            return result.toString();
        } catch (Exception e) {
            log.error("bind mobile fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("mobile", mobile);
            userOperationLog.putOtherMessage("sgid", sgid);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /**
     * module黑名单接口
     * <p/>
     * 数据格式 数据全量(增量)，获取增量数据的偏移标志位  黑名单接口调用间隔(单位秒)
     *
     * @param request
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/moduleblacklist", method = RequestMethod.GET)
    @ResponseBody
    public String moduleBlackList(HttpServletRequest request, ModuleBlackListParams params) throws Exception {
        Result result = new APIResultSupport(false);

        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                return result.toString();
            }
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }


            //有效期 （当前时间+60秒）秒
//            long expireTime = (System.currentTimeMillis() / 1000) + EXPIRE_TIME;

            //获取Redis中保存的黑名单数据
            Set<String> set = redisUtils.smember(CacheConstant.CACHE_KEY_BLACKLIST);
            for(int i=0;i<CacheConstant.BLACKLIST_SET_SIZE;i++){
                set.addAll(redisUtils.smember(CacheConstant.CACHE_KEY_BLACKLIST +i));
            }

//            if (params.getIs_delta() != 0 || params.getIs_delta() != 1) {
//                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//                return result.toString();
//            }

            //接口调用频率
//            int update_internal;
//            if (params.getUpdate_interval() == 0) {
//                update_internal = 10;
//            } else {
//                update_internal = params.getUpdate_interval();
//            }

            //  module 限制 update_internal 最小调用频次为 10s
            StringBuffer resultText = new StringBuffer("0 0 10");
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String str = it.next();
                String strs[] = str.split(BLACK_USER_EXPIRETIME_REDIS_SPLIT);
                resultText.append(BLACK_USER_LIST_VALUE_SPLIT).append(strs[0]).append(BLACK_USER_EXPIRETIME_SPLIT).append(strs[1]);
            }

            result.setSuccess(true);
            return resultText.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog("", String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(request));
            UserOperationLogUtil.log(userOperationLog);
        }
    }
    
    
    /**
     * 找回密码时获取用户安全信息
     * 返回的信息包含密保手机、密保邮箱、及密保问题（找回密码不会用到此返回结果）
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getsecinfo", method = RequestMethod.POST)
    @ResponseBody
    public String querySecureInfo(HttpServletRequest request, BaseUserApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        //用户输入的账号
        String passportId = params.getUserid();
        int clientId = params.getClient_id();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            
            //默认是sogou.com
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(passportId);
            
            if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
                passportId = passportId + CommonConstant.SOGOU_SUFFIX;
            }
            //查询主账号：@sogou.com/外域/第三方账号返回原样，手机账号返回绑定的主账号，若无主账号则返回手机号+@sohu.com
            passportId = commonManager.getPassportIdByUsername(passportId);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            switch (domain) {
                case THIRD:
                case UNKNOWN:
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result.toString();
            }
            result.setDefaultModel("userid", passportId);
            boolean checkTimes = resetPwdManager.checkFindPwdTimes(passportId).isSuccess();
            if (!checkTimes) {
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
                return result.toString();
            }
            result = registerApiManager.checkUser(passportId, clientId,true);//允许搜狐账号
            if (result.isSuccess()) {
                result.setSuccess(false);
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result.toString();
            }
            result = secureManager.queryAccountSecureInfo(passportId, clientId, true);
            if (!result.isSuccess()) {
                return result.toString();
            }
            AccountSecureInfoVO accountSecureInfoVO = (AccountSecureInfoVO) result.getDefaultModel();
            //记录找回密码次数
            resetPwdManager.incFindPwdTimes(passportId);
            
            // 转换安全信息
          // 如果用户的密保手机和密保邮箱存在，则返回模糊处理的手机号/密保邮箱及完整手机号/邮箱加密后的md5串
          if (accountSecureInfoVO != null) {
            String sec_mobile = (String) result.getModels().get("sec_mobile");
            String sec_email = (String) result.getModels().get("sec_email");
            if (AccountDomainEnum.OTHER.equals(domain)) {
              if (!passportId.equals(sec_email)) { //如果passportId是外域，则注册邮箱是它本身,当注册邮箱和密保邮箱不一样时，才返回注册邮箱
                result.setDefaultModel("reg_process_email", accountSecureInfoVO.getReg_email());
                result.setDefaultModel("reg_email_md5", DigestUtils.md5Hex(passportId));
              }
            }
            if (!Strings.isNullOrEmpty(sec_mobile)) {
              result.setDefaultModel("sec_process_mobile", accountSecureInfoVO.getSec_mobile());
              result.setDefaultModel("sec_mobile_md5", DigestUtils.md5Hex(sec_mobile.getBytes()));
              result.getModels().remove("sec_mobile"); //为了账号安全，不返回完整的手机号
            }
            if (!Strings.isNullOrEmpty(sec_email)) {
              result.setDefaultModel("sec_process_email", accountSecureInfoVO.getSec_email());
              result.setDefaultModel("sec_email_md5", DigestUtils.md5Hex(sec_email.getBytes()));
              result.getModels().remove("sec_email"); //为了账号安全，不返回完整的密保邮箱
            }
          }
            
            result.setDefaultModel("scode", commonManager.getSecureCode(passportId, clientId,CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            logger.error("querySecureInfo Is Failed,Username is " + passportId, e);
        } finally {
            //用户登录log
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(),
                                                                     String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), result.getCode(), getIp(request));
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }
    
    /**
     * 修改密码
     *
     * @param updateParams 传入的参数
     */
    @InterfaceSecurity
    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePwd(HttpServletRequest request, UpdatePswParams updateParams)
      throws Exception {
        Result result = new APIResultSupport(false);
    
        // 参数校验
        String validateResult = ControllerHelper.validateParams(updateParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
    
        String passportId = updateParams.getUserid();
        String clientId = String.valueOf(updateParams.getClient_id());
        String password = updateParams.getPassword();
        String newPwd = updateParams.getNewpwd();
        String ip = updateParams.getIp();
        
        try {
            // 用户名的所属域
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(passportId);
            if(THIRD.equals(accountDomainEnum) && !passportId.matches(".+@qq\\.sohu\\.com$")) {   // 第三方登陆
                // 非 QQ 第三方账号不允许此操作
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTALLOWED);
                return result.toString();
            }
    
            UpdatePwdParameters updatePwdParameters = new UpdatePwdParameters();
            updatePwdParameters.setClient_id(clientId);
            updatePwdParameters.setPassport_id(passportId);
            updatePwdParameters.setPassword(password);
            updatePwdParameters.setNewpwd(newPwd);
            updatePwdParameters.setIp(ip);
            
            result = secureManager.updateWebPwd(updatePwdParameters);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), clientId, result.getCode(), ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
