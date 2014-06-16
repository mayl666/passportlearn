package com.sogou.upd.passport.service.dataimport.sohu_nickname_migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.dataimport.util.ParamUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * 搜狐昵称迁移Task
 * <p/>
 * 失败结果解析：
 * <p/>
 * A：调用搜狐接口超时、或者其他错误
 * B：插入到u_p_m 昵称映射表失败
 * C：
 * <p/>
 * User: chengang
 * Date: 14-6-13
 * Time: 下午7:00
 */
public class SoHuNNMigrationTask extends RecursiveTask<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoHuNNMigrationTask.class);

    private static final long serialVersionUID = 5828962868075394870L;

    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private AccountDAO accountDAO;

    private DBShardRedisUtils dbShardRedisUtils;

    private String file;

    public SoHuNNMigrationTask(UniqNamePassportMappingDAO uniqNamePassportMappingDAO, AccountDAO accountDAO,
                               DBShardRedisUtils dbShardRedisUtils, String file) {
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
        this.accountDAO = accountDAO;
        this.dbShardRedisUtils = dbShardRedisUtils;
        this.file = file;
    }


    @Override
    protected List<String> compute() {
        List<String> failList = Lists.newLinkedList();

        Path dataPath = Paths.get(file);
        try (BufferedReader reader = Files.newBufferedReader(dataPath, Charset.defaultCharset())) {
            String passportId;
            while ((passportId = reader.readLine()) != null) {

                //构建参数
                RequestModelXml requestModelXml = ParamUtil.buildRequestModelXml(passportId);

                Map<String, Object> mapB = null;
                //昵称
                String nickName = StringUtils.EMPTY;
                try {
                    mapB = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);

                    int status = Integer.parseInt(mapB.get("status").toString());
                    if (status == 0) {
                        nickName = mapB.get("uniqname").toString();
                        if (!Strings.isNullOrEmpty(nickName)) {
                            if (StringUtils.contains(nickName, "在搜狐")) {
                                nickName = StringUtils.substringBefore(nickName, "在搜狐");
                            }
                            if (StringUtils.contains(nickName, "的blog")) {
                                nickName = StringUtils.substringBefore(nickName, "的blog");
                            }
                        }
                    } else {
                        failList.add("A:" + passportId + "STATUS:" + status);
                    }
                } catch (Exception e) {
                    failList.add("A:" + passportId);
                    LOGGER.error("SoHuNNMigrationTask get account nickname from SH error.", e);
                    continue;
                }

                try {
                    //根据昵称 查询 u_p_m 昵称表，校验昵称是否存在
                    String u_p_m_passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(nickName);
                    if (Strings.isNullOrEmpty(u_p_m_passportId)) {
                        //插入u_p_m 映射表
                        int insertUpmResult = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(nickName, passportId);
                        if (insertUpmResult > 0) {
                            //更新u_p_m缓存
//                            String cacheKey = CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID + nickName;
//                            redisUtils.setWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                        } else {
                            failList.add("B:" + passportId);
                        }
                    } else if (!u_p_m_passportId.equalsIgnoreCase(passportId)) {
                        //昵称已经存在，已经被占用，则跳过! “|” 前面是test库中 userid、后面是 upm中占用此昵称的 userid
                        failList.add("C:" + passportId + " | " + u_p_m_passportId);
                    }
                } catch (Exception e) {
                    LOGGER.error("SoHuNNMigrationTask operation u_p_m error.", e);
                    continue;
                }

                //更新account表 账号昵称数据
                try {
                    Account account = accountDAO.getAccountByPassportId(passportId);
                    if (account != null) {
                        if (Strings.isNullOrEmpty(account.getUniqname())) {
                            int updateAccountNickName = accountDAO.updateUniqName(nickName, passportId);
                            if (updateAccountNickName > 0) {
                                //更新缓存
                                account.setUniqname(nickName);
                                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
                                dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                            } else {
                                //更新account表昵称失败记录
                                failList.add("D:" + passportId);
                            }
                        } else {
                            //account表不存在对应的账号
                            failList.add("E:" + passportId);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("SoHuNNMigrationTask operation account error.", e);
                    continue;
                }
            }
        } catch (Exception e) {
            LOGGER.error("SoHuNNMigrationTask error.", e);
        }
        return failList;
    }
}
