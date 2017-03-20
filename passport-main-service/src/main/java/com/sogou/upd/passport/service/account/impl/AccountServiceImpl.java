package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.parameter.SohuPasswordType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CaptchaUtils;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.dal.routing.SGRoutingConfigurator;
import com.sogou.upd.passport.dao.dal.routing.SGStringHashRouter;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountHelper;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;
import com.xiaomi.common.service.dal.routing.Router;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final int expire = 30 * DateAndNumTimesConstant.ONE_DAY_INSECONDS;

    private static final String PASSPORT_ACTIVE_EMAIL_URL = "http://account.sogou.com/web/activemail?";
    private static final String POSTFIX_PINYIN_MIGRATE = "_pinyinPP_";   //SOGOU输入法昵称迁移，为保证昵称唯一性加的后缀
    private String POSTFIX_PINYIN_FORMAT = "(.+)" + POSTFIX_PINYIN_MIGRATE + "[0-9][0-9][0-9][0-9]$"; //加后缀的昵称，虽然在redis和数据库中存有后缀，返回给接口时要去掉

    /** 密码正则 */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z-_!@#%&*|?+\\[\\]\\{\\},.;:]{6,16}$");

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private CaptchaUtils captchaUtils;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private UniqNamePassportMappingService uniqNamePassportMappingService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private SGRoutingConfigurator sgRoutingConfigurator;

    /**
     * 分表路由
     */
    private static Router router;

    @Override
    public Account initialEmailAccount(String username, String ip) throws ServiceException {
        Account account;
        String cacheKey;
        try {
            cacheKey = buildAccountKey(username);
            account = dbShardRedisUtils.getObject(cacheKey, Account.class);
            if (account != null) {
                account.setFlag(AccountStatusEnum.REGULAR.getValue());
                long id = accountDAO.insertAccount(username, account);
                if (id > 0) {
                    account.setId(id);
                    //更新缓存，成为正式账户
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                    return account;
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            //删除激活
            cacheKey = buildCacheKey(username);
            redisUtils.delete(cacheKey);
        }
        return null;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initialAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider) throws ServiceException {
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign;
        try {
            if (!Strings.isNullOrEmpty(password) && !AccountTypeEnum.isConnect(provider)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, needMD5);
                account.setPassword(passwordSign);
            }
            account.setRegTime(new Date());
            account.setRegIp(ip);
            account.setAccountType(provider);
            account.setFlag(AccountStatusEnum.REGULAR.getValue());
            //增加 短信登录类型
            if (AccountTypeEnum.isConnect(provider) || AccountTypeEnum.isSOHU(provider)) {
                //对于第三方账号和sohu域账号来讲，无密码  搜狗账号迁移完成后，需要增加一个值表示无密码
                account.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
            } else {
                //其它新注册的搜狗账号密码类型都为2，即需要加盐
                account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            }
            String mobile = null;
            if (AccountTypeEnum.isPhone(username, provider)) {
                mobile = username;
            }
            account.setMobile(mobile);
                //正式注册到account表中
            long id = accountDAO.insertAccount(passportId, account);
            if (id > 0) {
                //手机注册时，写mobile与passportId映射表
                if (PhoneUtil.verifyPhoneNumberFormat(passportId.substring(0, passportId.indexOf("@")))) {
                    boolean row = mobilePassportMappingService.initialMobilePassportMapping(mobile, passportId);
                    if (!row) {
                        return null;
                    }
                }
                account.setId(id);
                String cacheKey = buildAccountKey(passportId);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return account;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initAccount(Account account) throws ServiceException {
        boolean initSuccess = false;
        try {
            long id = accountDAO.insertAccount(account.getPassportId(), account);
            if (id > 0) {
                initSuccess = true;
                account.setId(id);
                String cacheKey = buildAccountKey(account.getPassportId());
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return initSuccess;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return initSuccess;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initSOHUAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initSOHUAccount(String passportId, String ip) throws ServiceException {
        try {
            if (!AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                //只支持sohu域账号初始化操作
                return false;
            }
            Account account = queryAccountByPassportId(passportId);
            if (account != null) {
                return true;
            }
            account = initialAccount(passportId, null, false, ip, AccountTypeEnum.SOHU.getValue());
            if (account != null) {
                if (accountInfoService.updateAccountInfo(new AccountInfo(passportId, new Date(), new Date())))
                    return true;
                return false;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public Account queryAccountByPassportIdInCache(String passportId) throws ServiceException {
        Account account;
        try {
            String cacheKey = buildAccountKey(passportId);
            // just for the email register process if the customer want to resend the active email
            account = dbShardRedisUtils.getObject(cacheKey, Account.class);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return account;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        Account account;
        try {
            String cacheKey = buildAccountKey(passportId);
            account = dbShardRedisUtils.getObject(cacheKey, Account.class);
            if (account == null || account.getId() == 0) {
                account = accountDAO.getAccountByPassportId(passportId);
                if (account != null) {
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        //如果昵称符合POSTFIX_PINYIN_FORMAT，为输入法迁移数据，去掉后缀再返回
        if (null != account) {
            String uniqname = account.getUniqname();
            if (!Strings.isNullOrEmpty(uniqname)) {
                if (uniqname.matches(POSTFIX_PINYIN_FORMAT)) {
                    int endIndex = uniqname.indexOf(POSTFIX_PINYIN_MIGRATE);
                    account.setUniqname(uniqname.substring(0, endIndex));
                }
            }

        }

        //TODO:修复迁移过程中的脏数据,后续去掉
        if(null!=account){
            AccountDomainEnum accountDomain=AccountDomainEnum.getAccountDomain(passportId);
            if(accountDomain==AccountDomainEnum.SOHU){
                int pwdType=account.getPasswordtype();
                if(pwdType!=5){
                    account.setPasswordtype(5);
                    logger.warn("SOHU DIRTY DATA:"+passportId);
                }
            }
        }


        return account;
    }

    @Override
    public Result verifyUserPwdVaild(String passportId, String password, boolean needMD5,SohuPasswordType sohuPwdType) throws ServiceException {
        Result result = new APIResultSupport(false);
        Account userAccount;
        try {
            userAccount = queryAccountByPassportId(passportId);
        } catch (ServiceException e) {
            throw e;
        }
        try {
            if (userAccount == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (AccountHelper.isDisabledAccount(userAccount)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED);
                return result;
            }
            if (AccountHelper.isKilledAccount(userAccount)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                return result;
            }
            result = verifyUserPwdValidByPasswordType(userAccount, password, needMD5,sohuPwdType);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Result verifyUserPwdValidByPasswordType(Account account, String password, Boolean needMD5,SohuPasswordType sohuPwdType) {
        Result result = new APIResultSupport(false);
        String passwordType = String.valueOf(account.getPasswordtype());
        String storedPwd = account.getPassword();
        boolean pwdIsTrue = false;
        try {
            switch (Integer.parseInt(passwordType)) {
                case 0:   //原密码
                    pwdIsTrue = verifyPwdWithOriginal(password, storedPwd);
                    break;
                case 1:   //MD5
                    pwdIsTrue = verifyPwdWithMD5(password, storedPwd);
                    break;
                case 2:   //Crypt(password,salt)
                case 4:   // 第三方登陆账号
                    pwdIsTrue = PwdGenerator.verify(password, needMD5, storedPwd);
                    break;
                case 5:     //sohu crypt
                    pwdIsTrue= PwdGenerator.verifySohuPwd(storedPwd,password,sohuPwdType);
            }
            if (pwdIsTrue) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                result.setDefaultModel("userid", account.getPassportId());
                result.setDefaultModel("uniqName", account.getUniqname());
                result.setDefaultModel(account);
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                return result;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private boolean verifyPwdWithOriginal(String password, String storedPwd) {
        if (storedPwd.equals(password)) {
            return true;
        }
        return false;
    }

    private boolean verifyPwdWithMD5(String password, String storedPwd) {
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        if (storedPwd.equals(pwdMD5)) {
            return true;
        }
        return false;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Account queryNormalAccount(String passportId) throws ServiceException {
        Account account = queryAccountByPassportId(passportId);
        if (account != null && AccountHelper.isNormalAccount(account)) {
            return account;
        }
        return null;
    }

    private String buildResetPwdCacheKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
    }

    @Override
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException {
        try {
            int row = accountDAO.deleteAccountByPassportId(passportId);
            if (row != 0) {

                // 将此用户的所有登录态删除
                deleteSgid(passportId);

                String cacheKey = buildAccountKey(passportId);
                long redisRow = dbShardRedisUtils.delete(cacheKey);
                return redisRow == 1;
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_resetPassword", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean resetPassword(String sohuPassportId,Account account, String password, boolean needMD5) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            AccountDomainEnum accountDomain=AccountDomainEnum.getAccountDomain(passportId);
            if(accountDomain==AccountDomainEnum.SOHU){
                passportId=sohuPassportId;//sohu 账号区分大小写
            }
            String passwdSign = PwdGenerator.generatorStoredPwd(password, needMD5);
            int row = accountDAO.updatePassword(passwdSign, passportId);
            pcAccountTokenService.batchRemoveAccountToken(passportId, true);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setPassword(passwdSign);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                //输入法泄露数据处理，修改密码后解除限制
                removeLeakUser(account, passportId);

                // 将此用户的所有登录态删除
                deleteSgid(account);

                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    public boolean isPasswordStrengthStrong(int clientId, String password) {
        if (clientId == 2011 || clientId == 2020 || clientId == 10069) { // 对糖猫进行特殊处理
            return PASSWORD_PATTERN.matcher(password).matches();
        }
        return true;
    }

    /**
     * 删除登录态
     * @param passportId
     */
    public void deleteSgid(String passportId) throws ServiceException {
        Account account = new Account();
        account.setPassportId(passportId);
        deleteSgid(account);
    }

    /**
     * 删除登录态
     * @param account
     */
    public void deleteSgid(Account account) throws ServiceException {
        String prefix = getAccountPrefix(account);
        String cacheKey = buildSessionKey(prefix);

        dbShardRedisUtils.delete(cacheKey);
    }

    public String getAccountPrefix(String passportId) throws ServiceException {
        Account account = new Account();
        account.setPassportId(passportId);
        return getAccountPrefix(account);
    }
    public String getAccountPrefix(Account account) throws ServiceException {
        if(account == null || StringUtils.isBlank(account.getPassportId())) {
            throw new ServiceException("账号信息错误");
        }

        if(account.getId() == 0) {
            account = queryAccountByPassportId(account.getPassportId());
        }

        // 计算分表索引
        String routeResult = getRouter().doRoute(account.getPassportId());
        int index = routeResult.lastIndexOf('_') + 1;
        routeResult = routeResult.substring(index);

        return routeResult + "-" + account.getId();
    }

    private String buildSessionKey(String prefix) {
        return CacheConstant.CACHE_PREFIX_SESSION + prefix;
    }

    /**
     * 获取路由
     * @return
     */
    private Router getRouter() {
        if(router == null) {
            router = createRouter();
        }
        return router;
    }

    /**
     * 创建分表路由
     * @return
     */
    private Router createRouter() {
        // 获取数据库分表策略
        String partition = sgRoutingConfigurator.getPartitions().get(0);
        String[] conf = partition.split(":");
        if (SGRoutingConfigurator.SG_STRING_HASH.equalsIgnoreCase(conf[0])) {
            SGRoutingConfigurator.RouterFactory factory = new SGRoutingConfigurator.RouterFactory(SGRoutingConfigurator.SG_STRING_HASH) {
                @Override
                public Router onCreateRouter(String column, String pattern, int partitions) {
                    return new SGStringHashRouter(column, pattern, partitions);
                }
            };
            return factory.setColumn(conf[2]).setPattern(conf[3]).setPartition(conf[4]).createRouter();
        }
        return null;
    }

    @Override
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip, String ru) throws ServiceException {
        return sendActiveEmail(username, passpord, clientId, ip, ru, true, null);
    }

    @Override
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip, String ru, boolean rtp, String lang) throws ServiceException {
        boolean flag = true;
        try {
            String token = SecureCodeGenerator.generatorSecureCode(username, clientId);
            String activeUrl =
                    PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
                            "&client_id=" + clientId +
                            "&token=" + token;
            if (Strings.isNullOrEmpty(ru)) {
                ru = CommonConstant.DEFAULT_INDEX_URL;
            }
            activeUrl += "&ru=" + Coder.encodeUTF8(ru);
            activeUrl += "&rtp=" + rtp;
            String cacheKey = buildCacheKey(username);
            Map<String, String> mapParam = new HashMap<>();
            //设置连接失效时间
            mapParam.put("token", token);
            //设置ru
            mapParam.put("ru", ru);
            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);
            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            activeEmail.setMap(map);
            if(StringUtils.equalsIgnoreCase(lang, "en")) {
                activeEmail.setTemplateFile("activemail-en.vm");
                activeEmail.setSubject("activate your sogou account");
            } else {
                activeEmail.setTemplateFile("activemail.vm");
                activeEmail.setSubject("激活您的搜狗通行证帐户");
            }
            activeEmail.setCategory("register");
            activeEmail.setToEmail(username);
            mailUtils.sendEmail(activeEmail);
            //如果重新发送激活邮件，password是为空的，说明不是注册，否则需要临时注册到缓存
            if (!Strings.isNullOrEmpty(passpord)) {
                //临时注册到缓存
                initialAccountToCache(username, passpord, ip);
            }
            redisUtils.hPutAll(cacheKey, mapParam);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);
        } catch (Exception e) {
            logger.error("username:{} send email fail!", username, e);
            flag = false;
        }
        return flag;
    }

    private String buildCacheKey(String username) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
    }

    @Override
    public Map<String, String> getActiveInfo(String username) {
        String cacheKey = buildCacheKey(username);
        Map<String, String> mapParam = redisUtils.hGetAll(cacheKey);
        return mapParam;

    }

    @Override
    public boolean activeEmail(String username, String token, int clientId) throws ServiceException {
        try {
            String cacheKey = buildCacheKey(username);
            String tokenCache;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                Map<String, String> mapParam = getActiveInfo(username);
                if (!mapParam.isEmpty()) {
                    tokenCache = mapParam.get("token");
                    if (tokenCache.equals(token)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean setCookie() throws Exception {
//    ServletUtil.setCookie();
        return false;
    }

    @Override
    public Map<String, Object> getCaptchaCode(String token) throws ServiceException {
        Map<String, Object> map;
        try {
            if (Strings.isNullOrEmpty(token)) {
                token = UUID.randomUUID().toString().replaceAll("-", "");
            }
            String cacheKey = buildCaptchaCacheKey(token);
            //生成验证码
            map = captchaUtils.getRandCode();

            if (map != null && map.size() > 0) {

                String captchaCode = (String) map.get("captcha");
                map.put("token", token);

                redisUtils.setWithinSeconds(cacheKey, captchaCode, DateAndNumTimesConstant.CAPTCHA_INTERVAL);
            } else {
                map = Maps.newHashMap();
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return map;
    }

    private String buildCaptchaCacheKey(String token) {
        return CacheConstant.CACHE_PREFIX_UUID_CAPTCHA + token;
    }


    @Override
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode) throws ServiceException {
        try {
            String cacheKey = buildCaptchaCacheKey(token);
            String captchaCodeCache = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(captchaCodeCache) && captchaCodeCache.equalsIgnoreCase(captchaCode)) {
                redisUtils.delete(cacheKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            // throw new ServiceException(e);
            return false;
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_modifyMobile", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean modifyMobileByAccount(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateMobile(newMobile, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setMobile(newMobile);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean bindMobile(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            String oldMobile = account.getMobile();
            String newMobilePassportId = mobilePassportMappingService.queryPassportIdByMobile(newMobile);
            if (!Strings.isNullOrEmpty(newMobilePassportId)) { //该手机号不允许绑定
                return false;
            }
            if (!Strings.isNullOrEmpty(oldMobile)) { //已绑定过手机的账号不允许再次绑定
                return false;
            }
            //这里要先写account再写mapping，因为根据account的mobile判断是否已绑定手机
            boolean isModifyAccount = modifyMobileByAccount(account, newMobile);
            boolean isInitMapping = mobilePassportMappingService.initialMobilePassportMapping(newMobile, passportId);
            return isInitMapping && isModifyAccount;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean modifyMobile(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateMobile(newMobile, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setMobile(newMobile);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean modifyBindMobile(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            String oldMobile = account.getMobile();
            String newMobilePassportId = mobilePassportMappingService.queryPassportIdByMobile(newMobile);
            if (!Strings.isNullOrEmpty(newMobilePassportId)) { //该手机号不允许绑定
                return false;
            }
            if (Strings.isNullOrEmpty(oldMobile)) { //未绑定过手机的账号不允许修改绑定
                return false;
            }
            //修改绑定手机
            String oldMobilePassportId = mobilePassportMappingService.queryPassportIdByMobile(oldMobile);
            if (!Strings.isNullOrEmpty(oldMobilePassportId)) {
                boolean isDeleteMapping = mobilePassportMappingService.deleteMobilePassportMapping(oldMobile);
                if (!isDeleteMapping) return false;
            } else { //上一次绑定失败，写account成功，写mapping失败
                logger.error("before bind account success but mapping fail, passportId:" + passportId + ", newMobile:" + newMobile);
                return false;
            }
            //这里要先写account再写mapping，因为根据account的mobile判断是否已绑定手机
            boolean isModifyAccount = modifyMobileByAccount(account, newMobile);
            boolean isInitMapping = mobilePassportMappingService.initialMobilePassportMapping(newMobile, passportId);
            return isInitMapping && isModifyAccount;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteOrUnbindMobile(String mobile) throws ServiceException {
        String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
        try {
            if (!Strings.isNullOrEmpty(passportId)) {
                Account account = queryAccountByPassportId(passportId);
                if (account != null) {
                    boolean isAccount;
                    if (passportId.equals(mobile + "@sohu.com")) { //删除注册手机号
                        isAccount = deleteAccountByPassportId(passportId);
                    } else { //删除手机号绑定关系
                        isAccount = modifyMobileByAccount(account, null);
                    }
                    boolean isDeleteMapping = mobilePassportMappingService.deleteMobilePassportMapping(mobile);
                    return isAccount && isDeleteMapping;
                }
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_updateState", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean updateState(Account account, int newState) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateState(newState, passportId);
            if (row > 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setFlag(newState);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    /**
     * 调SOHU接口注册前，未激活的外域邮箱先写SG缓存，保存时间为两天，再调
     *
     * @param username
     * @param password
     * @param ip
     * @throws ServiceException
     */
//    @Override
//    public void initialMailToCache(String username, String password, String ip) throws ServiceException {
//        Account account = initialAccountToCache(username, password, ip);
//        String cacheKey = buildAccountKey(username);
//        dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);
//    }

    /*
     * 外域邮箱注册
     */
    public Account initialAccountToCache(String username, String password, String ip) throws ServiceException {
        int provider = AccountTypeEnum.EMAIL.getValue();
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, true);
            }
            account.setPassword(passwordSign);
            account.setRegTime(new Date());
            account.setAccountType(provider);
            account.setFlag(AccountStatusEnum.DISABLED.getValue());
            account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            account.setRegIp(ip);
            String cacheKey = buildAccountKey(username);
            dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return account;
    }

    protected String buildAccountKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }


    @Override
    public boolean checkCaptchaCode(String token, String captchaCode) throws ServiceException {
        try {
            //校验验证码
            if (!checkCaptchaCodeIsVaild(token, captchaCode)) {
                return false;
            }
        } catch (ServiceException e) {
            logger.error("checkCaptchaCode fail", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateUniqName(Account account, String uniqname) throws ServiceException {
        try {
            String oldUniqName = account.getUniqname();
            String passportId = account.getPassportId();
            //如果新昵称不为空且与原昵称不重复，则更新数据库及缓存
            if (!Strings.isNullOrEmpty(uniqname) && !uniqname.equals(oldUniqName)) {
                //先检查 u_p_m 中是否存在，如果昵称存在则返回
                if (!Strings.isNullOrEmpty(uniqNamePassportMappingService.checkUniqName(uniqname))) {
                    return false;
                }
                //更新数据库
                int row = accountDAO.updateUniqName(uniqname, passportId);
                if (row > 0) {
                    String cacheKey = buildAccountKey(passportId);
                    account.setUniqname(uniqname);
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                    //新昵称写入映射表
                    if (!uniqNamePassportMappingService.insertUniqName(passportId, uniqname)) {
                        return false;
                    }
                    //旧昵称在映射表里有记录则删除
                    if (!Strings.isNullOrEmpty(uniqNamePassportMappingService.checkUniqName(uniqname))) {
                        uniqNamePassportMappingService.removeUniqName(oldUniqName);
                    }
                    return true;
                }
            } else {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_updateAvatar", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean updateAvatar(Account account, String avatar) {
        try {
            String passportId = account.getPassportId();
            //更新数据库
            int row = accountDAO.updateAvatar(avatar, passportId);
            if (row > 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setAvatar(avatar);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    //输入法泄露数据处理，修改密码后解除限制
    public void removeLeakUser(Account account, String passportId) {
        String leakKey = null;
        try {
            String cacheKey = buildAccountKey(passportId);
            leakKey = CacheConstant.CACHE_PREFIX_USER_LEAKLIST + account.getPassportId();
            if (account.getFlag() == AccountStatusEnum.LEAKED.getValue()) {
                dbShardRedisUtils.delete(cacheKey);
                accountDAO.updateState(AccountStatusEnum.REGULAR.getValue(), passportId);
            }
            if (redisUtils.checkKeyIsExist(leakKey)) {
                redisUtils.delete(leakKey);
            }
        } catch (Exception e) {
            logger.error("sogou leak passportid reset passport handle error : " + passportId);
        }
    }

}
