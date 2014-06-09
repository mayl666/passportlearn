package com.sogou.upd.passport.service.dataimport.nick_name_migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-7
 * Time: 上午11:28
 */
public class MigrationTask extends RecursiveTask<List<String>> {
//public class MigrationTask extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationTask.class);

    private static final long serialVersionUID = -3301260887405142613L;

    private static int start;

    private static final int LIMIT_HOLD = 150000;
//    private static final int LIMIT_HOLD = 100;

    //    @Autowired
    private AccountDAO accountDAO;

    //    @Autowired
    private AccountBaseInfoDAO baseInfoDAO;

    //    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    //    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;


    List<String> taskFailList = Lists.newLinkedList();

    //更新db 失败记录
    List<String> updateDBFailList = Lists.newLinkedList();

    //非第三方账号不存在 记录
    List<String> accountNotExistList = Lists.newLinkedList();

    //sohu 矩阵账号，插入db 失败记录
    List<String> insertSoHuAccountFailList = Lists.newLinkedList();

    List<String> upmNickNameNotExistList = Lists.newLinkedList();

    List<String> baseUpmNickNameNotMatchList = Lists.newLinkedList();


    private static final String DATA_STORE_PATH = "D:\\项目\\非第三方账号迁移\\内部昵称数据迁移\\account_base_info\\";


    public MigrationTask(AccountBaseInfoDAO baseDao, AccountDAO accountDao, UniqNamePassportMappingDAO uniqNamePassportMappingDAO, DBShardRedisUtils shardRedisUtils, int start) {
        this.baseInfoDAO = baseDao;
        this.accountDAO = accountDao;
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
        this.dbShardRedisUtils = shardRedisUtils;
        this.start = start;
    }


    /**
     * 判断账号类型：搜狗账号、手机账号、外域邮箱账号、
     * 搜狐矩阵账号：生成一条无密码的记录
     *
     * @return
     */
    @Override
    protected List<String> compute() {
        try {
            List<AccountBaseInfo> accountBaseInfoList = baseInfoDAO.getNotThirdPartyAccountByPage(start, LIMIT_HOLD);
            if (accountBaseInfoList != null && accountBaseInfoList.size() > 0) {
                for (AccountBaseInfo accountBaseInfo : accountBaseInfoList) {
                    String passportId = accountBaseInfo.getPassportId();
                    String nickName = accountBaseInfo.getUniqname();
                    String avatar = accountBaseInfo.getAvatar();
                    String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
                    //账号类型
                    AccountDomainEnum domainEnum = AccountDomainEnum.getAccountDomain(passportId);
                    try {
                        Account account = accountDAO.getAccountByPassportId(passportId);
                        //关键点 base 表昵称是否为空 ，经过排查数据 base表中 非第三方账号存在 66561条数据 昵称为空
                        if (!Strings.isNullOrEmpty(nickName)) {
                            //检查 u_p_m 昵称映射表中是否已经存在昵称
                            String temp_passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(nickName);
                            if (account != null) {
                                if (!Strings.isNullOrEmpty(temp_passportId) && temp_passportId.equalsIgnoreCase(passportId)) {
                                    if (AccountDomainEnum.SOGOU == domainEnum || AccountDomainEnum.PHONE == domainEnum ||
                                            AccountDomainEnum.OTHER == domainEnum || AccountDomainEnum.SOHU == domainEnum) {
                                        try {
                                            //存在、则更新DB中账号的昵称和头像信息
                                            updateAccountNickNameAndAvatar(account, passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                                        } catch (Exception e) {
                                            LOGGER.error("update account error.", e);
                                            continue;
                                        }
                                    }
                                } else if (Strings.isNullOrEmpty(temp_passportId)) {
                                    //account base 中昵称在 u_p_m 映射表中查询不到，需要记录，记录account base中对应的账号
                                    upmNickNameNotExistList.add("D:" + passportId + " | " + nickName);
//                                    LOGGER.info(String.format("base has nickname upm not has passportId:{},nickName:{}"), passportId, nickName);
                                } else if (temp_passportId.equalsIgnoreCase(passportId)) {
                                    baseUpmNickNameNotMatchList.add("E:" + temp_passportId + " | " + passportId);
//                                    LOGGER.info(String.format("base and upm nickname not match passportId:{},nickName:{}"), temp_passportId + " | " + passportId, nickName);
                                }
                            } else if (AccountDomainEnum.SOHU == domainEnum) {
                                if (!Strings.isNullOrEmpty(temp_passportId) && temp_passportId.equalsIgnoreCase(passportId)) {
                                    //搜狐域账号
                                    //搜狐矩阵账号 插入一条无密码的记录
                                    try {
                                        initSoHuAccount(passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                                    } catch (Exception e) {
                                        LOGGER.error(" migration task insert sohu account error.", e);
                                        continue;
                                    }
                                } else if (Strings.isNullOrEmpty(temp_passportId)) {
                                    //account base 中昵称在 u_p_m 映射表中查询不到，需要记录，记录account base中对应的账号
                                    upmNickNameNotExistList.add("D:" + passportId + " | " + nickName);
//                                    LOGGER.info(String.format("base has nickname upm not has passportId:{},nickName:{}"), passportId, nickName);
                                } else if (temp_passportId.equalsIgnoreCase(passportId)) {
                                    baseUpmNickNameNotMatchList.add("E:" + temp_passportId + " | " + passportId);
//                                    LOGGER.info(String.format("base and upm nickname not match passportId:{},nickName:{}"), temp_passportId + " | " + passportId, nickName);
                                }
                            } else {
                                //在全量+增量数据导入完成后，对于非第三方账号数据应该都存在于account_0_32表中，若不存在，则记录Log
                                //account_not_exist.txt
                                accountNotExistList.add("C:" + passportId);
//                                LOGGER.info(" account_0-32 account not exist passportId:{}", passportId);
                            }
                        } else {
                            //base表 昵称为空，走此分支
                            if (account != null) {
                                if (AccountDomainEnum.SOGOU == domainEnum || AccountDomainEnum.PHONE == domainEnum ||
                                        AccountDomainEnum.OTHER == domainEnum || AccountDomainEnum.SOHU == domainEnum) {
                                    try {
                                        //存在、则更新DB中账号的昵称和头像信息
                                        updateAccountNickNameAndAvatar(account, passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                                    } catch (Exception e) {
                                        LOGGER.error("update account error.", e);
                                        continue;
                                    }
                                }
                            } else if (AccountDomainEnum.SOHU == domainEnum) {
                                //搜狐域账号
                                //搜狐矩阵账号 插入一条无密码的记录
                                try {
                                    initSoHuAccount(passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                                } catch (Exception e) {
                                    LOGGER.error(" migration task insert sohu account error.", e);
                                    continue;
                                }
                            } else {
                                //在全量+增量数据导入完成后，对于非第三方账号数据应该都存在于account_0_32表中，若不存在，则记录Log
                                //account_not_exist.txt
                                accountNotExistList.add("C:" + passportId);
//                                LOGGER.info(" account_0-32 account not exist passportId:{}", passportId);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("migration task update account error", e);
                        continue;
                    }
                }
            }

            //失败记录汇总
            taskFailList.addAll(updateDBFailList);
            taskFailList.addAll(insertSoHuAccountFailList);
            taskFailList.addAll(upmNickNameNotExistList);
            taskFailList.addAll(baseUpmNickNameNotMatchList);
            taskFailList.addAll(accountNotExistList);
        } catch (Exception e) {
            LOGGER.error("migration task failed.", e);
        }

        return taskFailList;
    }


    /**
     * 初始化搜狐域账号
     *
     * @param passportId
     * @param cacheKey
     * @param nickName
     * @param avatar
     * @param accountDAO
     * @param dbShardRedisUtils
     */
    private void initSoHuAccount(String passportId, String nickName, String avatar, AccountDAO accountDAO, DBShardRedisUtils dbShardRedisUtils, String cacheKey) {
        Account sohuAccount = new Account();
        sohuAccount.setPassportId(passportId);
        sohuAccount.setUniqname(nickName);
        sohuAccount.setAvatar(avatar);
        sohuAccount.setFlag(1);
        sohuAccount.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
        sohuAccount.setAccountType(AccountTypeEnum.SOHU.getValue());
        sohuAccount.setRegTime(new Date());

        int insertSohuAccountResult = accountDAO.insertAccount(passportId, sohuAccount);
        if (insertSohuAccountResult > 0) {
            //更新缓存
            dbShardRedisUtils.setWithinSeconds(cacheKey, sohuAccount, DateAndNumTimesConstant.ONE_MONTH);
        } else {
            //插入db失败、记录失败账号数据
            insertSoHuAccountFailList.add("B:" + passportId);
            LOGGER.info("insert sohu account failed. passportId:{}", passportId);
        }
    }


    /**
     * 更新账号 头像 昵称
     *
     * @param account
     * @param passportId
     * @param nickName
     * @param avatar
     * @param accountDAO
     * @param dbShardRedisUtils
     * @param cacheKey
     */
    private void updateAccountNickNameAndAvatar(Account account, String passportId, String nickName, String avatar, AccountDAO accountDAO, DBShardRedisUtils dbShardRedisUtils, String cacheKey) {
        //存在、则更新DB中账号的昵称和头像信息
        int updateDbResult = accountDAO.updateNickNameAndAvatar(nickName, avatar, passportId);
        if (updateDbResult > 0) {
            account.setUniqname(nickName);
            if (!Strings.isNullOrEmpty(avatar)) {
                account.setAvatar(avatar);
            }
            //更新缓存
            dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
        } else {
            //更新db失败、记录Log : fail_update_account.txt
            updateDBFailList.add("A:" + passportId);
            LOGGER.info(" update account fail passportId:{}", passportId);
        }
    }


    //    @Test
    public void testCheck() {
        String passportId = "libaitianming@sohu.com";
        String nickName = "";
        String avatar = "%s/app/a/%s/E4kdIWNcsIn7vsvl_1395505089656";
        String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
        //账号类型
        AccountDomainEnum domainEnum = AccountDomainEnum.getAccountDomain(passportId);
        try {
            Account account = accountDAO.getAccountByPassportId(passportId);
            //关键点 base 表昵称是否为空 ，经过排查数据 base表中 非第三方账号存在 66561条数据 昵称为空
            if (!Strings.isNullOrEmpty(nickName)) {
                //检查 u_p_m 昵称映射表中是否已经存在昵称
                String temp_passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(nickName);
                if (account != null) {
                    if (!Strings.isNullOrEmpty(temp_passportId) && temp_passportId.equalsIgnoreCase(passportId)) {
                        if (AccountDomainEnum.SOGOU == domainEnum || AccountDomainEnum.PHONE == domainEnum ||
                                AccountDomainEnum.OTHER == domainEnum || AccountDomainEnum.SOHU == domainEnum) {
                            try {
                                //存在、则更新DB中账号的昵称和头像信息
                                updateAccountNickNameAndAvatar(account, passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                            } catch (Exception e) {
                                LOGGER.error("update account error.", e);
                            }
                        }
                    } else if (Strings.isNullOrEmpty(temp_passportId)) {
                        //account base 中昵称在 u_p_m 映射表中查询不到，需要记录，记录account base中对应的账号
                        upmNickNameNotExistList.add("D:" + passportId);
                        LOGGER.info(String.format("base has nickname upm not has passportId:{},nickName:{}"), passportId, nickName);
                    } else if (temp_passportId.equalsIgnoreCase(passportId)) {
                        baseUpmNickNameNotMatchList.add("E:" + temp_passportId + " | " + passportId);
                        LOGGER.info(String.format("base and upm nickname not match passportId:{},nickName:{}"), temp_passportId + " | " + passportId, nickName);
                    }
                } else if (AccountDomainEnum.SOHU == domainEnum) {
                    if (!Strings.isNullOrEmpty(temp_passportId) && temp_passportId.equalsIgnoreCase(passportId)) {
                        //搜狐域账号
                        //搜狐矩阵账号 插入一条无密码的记录
                        try {
                            initSoHuAccount(passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                        } catch (Exception e) {
                            LOGGER.error(" migration task insert sohu account error.", e);
                        }
                    } else if (Strings.isNullOrEmpty(temp_passportId)) {
                        //account base 中昵称在 u_p_m 映射表中查询不到，需要记录，记录account base中对应的账号
                        upmNickNameNotExistList.add("D:" + passportId);
                        LOGGER.info(String.format("base has nickname upm not has passportId:{},nickName:{}"), passportId, nickName);
                    } else if (temp_passportId.equalsIgnoreCase(passportId)) {
                        baseUpmNickNameNotMatchList.add("E:" + temp_passportId + " | " + passportId);
                        LOGGER.info(String.format("base and upm nickname not match passportId:{},nickName:{}"), temp_passportId + " | " + passportId, nickName);
                    }
                } else {
                    //在全量+增量数据导入完成后，对于非第三方账号数据应该都存在于account_0_32表中，若不存在，则记录Log
                    //account_not_exist.txt
                    accountNotExistList.add("C:" + passportId);
                    LOGGER.info(" account_0-32 account not exist passportId:{}", passportId);
                }
            } else {
                //base表 昵称为空，走此分支
                if (account != null) {
                    if (AccountDomainEnum.SOGOU == domainEnum || AccountDomainEnum.PHONE == domainEnum ||
                            AccountDomainEnum.OTHER == domainEnum || AccountDomainEnum.SOHU == domainEnum) {
                        try {
                            //存在、则更新DB中账号的昵称和头像信息
                            updateAccountNickNameAndAvatar(account, passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                        } catch (Exception e) {
                            LOGGER.error("update account error.", e);
                        }
                    }
                } else if (AccountDomainEnum.SOHU == domainEnum) {
                    //搜狐域账号
                    //搜狐矩阵账号 插入一条无密码的记录
                    try {
                        initSoHuAccount(passportId, nickName, avatar, accountDAO, dbShardRedisUtils, cacheKey);
                    } catch (Exception e) {
                        LOGGER.error(" migration task insert sohu account error.", e);
                    }
                } else {
                    //在全量+增量数据导入完成后，对于非第三方账号数据应该都存在于account_0_32表中，若不存在，则记录Log
                    //account_not_exist.txt
                    accountNotExistList.add("C:" + passportId);
                    LOGGER.info(" account_0-32 account not exist passportId:{}", passportId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("migration task update account error", e);
        }
    }


    /**
     * 记录失败结果到文件
     */
    private static void writeResult2File(List<String> updateDBFailList, List<String> accountNotExistList, List<String> insertSoHuAccountFailList) {
        try {
            //对失败记录、写文件
            //更新db 失败记录写文件 task 号  fail_update_account_task.txt
            if (CollectionUtils.isNotEmpty(updateDBFailList)) {
                FileUtil.storeFile(DATA_STORE_PATH + "fail_update_account_task_" + start + ".txt", updateDBFailList);
            }

            //非第三方账号、在 account_0_32 表中不存在记录、写文件 task 号 account_not_exist_task.txt
            if (CollectionUtils.isNotEmpty(accountNotExistList)) {
                FileUtil.storeFile(DATA_STORE_PATH + "account_not_exist_task_" + start + ".txt", accountNotExistList);
            }

            //插入搜狐矩阵账号失败、写文件
            if (CollectionUtils.isNotEmpty(insertSoHuAccountFailList)) {
                FileUtil.storeFile(DATA_STORE_PATH + "fail_insert_sohu_account_task_" + start + ".txt", insertSoHuAccountFailList);
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("writeResult2File error", e);
        }

    }
}
