package com.sogou.upd.passport.service.dataimport.nick_name_migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationTask.class);

    private static final long serialVersionUID = -3301260887405142613L;

    private static int start;

    private static final int LIMIT_HOLD = 50000;

    private AccountDAO accountDAO;

    private AccountBaseInfoDAO baseInfoDAO;


    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private DBShardRedisUtils dbShardRedisUtils;


    List<String> taskFailList = Lists.newLinkedList();

    //更新db 失败记录
    List<String> updateDBFailList = Lists.newLinkedList();

    //非第三方账号不存在 记录
    List<String> accountNotExistList = Lists.newLinkedList();

    //sohu 矩阵账号，插入db 失败记录
    List<String> insertSoHuAccountFailList = Lists.newLinkedList();


    //在 u_p_m 映射表中已经存在的昵称信息
    Map<String, String> nickNameUpmExistMap = Maps.newConcurrentMap();


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
                    //先判断账号类型：非第三方账号、搜狐域账号
                    if (AccountDomainEnum.SOGOU == domainEnum || AccountDomainEnum.PHONE == domainEnum || AccountDomainEnum.OTHER == domainEnum) {
                        try {
                            Account account = accountDAO.getAccountByPassportId(passportId);
                            if (account != null) {
                                //检查 u_p_m 昵称映射表中是否已经存在昵称
                                String temp_passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(nickName);
                                if (Strings.isNullOrEmpty(temp_passportId)) {
                                    //存在、则更新DB中账号的昵称和头像信息
                                    int updateDbResult = accountDAO.updateNickNameAndAvatar(passportId, nickName, avatar);
                                    if (updateDbResult > 0) {
                                        account.setUniqname(nickName);
                                        if (!Strings.isNullOrEmpty(avatar)) {
                                            account.setAvatar(avatar);
                                        }
                                        //更新缓存
                                        dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.THREE_MONTH);
                                    } else {
                                        //更新db失败、记录Log : fail_update_account.txt
                                        updateDBFailList.add(passportId);
                                    }
                                } else {
                                    //account_base_info 表昵称
                                    nickNameUpmExistMap.put(temp_passportId, nickName);
                                }
                            } else {
                                //在全量+增量数据导入完成后，对于非第三方账号数据应该都存在于account_0_32表中，若不存在，则记录Log
                                //account_not_exist.txt
                                accountNotExistList.add(passportId);
                            }
                        } catch (Exception e) {
//                            updateDBFailList.add(passportId);
                            LOGGER.error("migration task update account error", e);
                            continue;
                        }
                    } else if (AccountDomainEnum.SOHU == domainEnum) {
                        //搜狐矩阵账号 插入一条无密码的记录
                        Account sohuAccount = new Account();
                        sohuAccount.setPassportId(passportId);
                        sohuAccount.setUniqname(nickName);
                        sohuAccount.setAvatar(avatar);
                        sohuAccount.setFlag(1);
                        sohuAccount.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
                        sohuAccount.setAccountType(AccountTypeEnum.SOHU.getValue());
                        sohuAccount.setRegTime(new Date());

                        int insertSohuAccountResult;
                        try {
                            insertSohuAccountResult = accountDAO.insertAccount(passportId, sohuAccount);
                            if (insertSohuAccountResult > 0) {
                                //更新缓存
                                dbShardRedisUtils.setWithinSeconds(cacheKey, sohuAccount, DateAndNumTimesConstant.THREE_MONTH);
                            }
                        } catch (Exception e) {
//                            insertSoHuAccountFailList.add(passportId);
                            LOGGER.error(" migration task insert sohu account error.", e);
                            continue;
                        }
                    }
                }
            }

            //记录失败记录到文件
            writeResult2File(updateDBFailList, accountNotExistList, insertSoHuAccountFailList);

            //失败记录汇总
            taskFailList.addAll(updateDBFailList);
            taskFailList.addAll(accountNotExistList);
            taskFailList.addAll(insertSoHuAccountFailList);
        } catch (Exception e) {
            LOGGER.error("migration task failed.", e);
        }
        return taskFailList;
    }


    /**
     * 记录失败结果到文件
     */
    private static void writeResult2File(List<String> updateDBFailList, List<String> accountNotExistList, List<String> insertSoHuAccountFailList) {
        try {
            //对失败记录、写文件
            //更新db 失败记录写文件 task 号  fail_update_account_task.txt
            if (CollectionUtils.isNotEmpty(updateDBFailList)) {
                FileUtil.storeFile("fail_update_account_task_" + start + ".txt", updateDBFailList);
            }

            //非第三方账号、在 account_0_32 表中不存在记录、写文件 task 号 account_not_exist_task.txt
            if (CollectionUtils.isNotEmpty(accountNotExistList)) {
                FileUtil.storeFile("account_not_exist_task_" + start + ".txt", accountNotExistList);
            }

            //插入搜狐矩阵账号失败、写文件
            if (CollectionUtils.isNotEmpty(insertSoHuAccountFailList)) {
                FileUtil.storeFile("fail_insert_sohu_account_task_" + start + ".txt", insertSoHuAccountFailList);
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("writeResult2File error", e);
        }

    }
}
