package com.sogou.upd.passport.service.dataimport.repair_write_separate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserExtInfoDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserInfoDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserOtherInfoDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.repairdata.IncUserExtInfo;
import com.sogou.upd.passport.model.repairdata.IncUserInfo;
import com.sogou.upd.passport.model.repairdata.IncUserOtherInfo;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午3:43
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class RepairWriteData extends BaseTest {

    private List<String> failedList = Lists.newArrayList();

    @Autowired
    private IncUserInfoDAO incUserInfoDAO;
    @Autowired
    private IncUserOtherInfoDAO incUserOtherInfoDAO;
    @Autowired
    private IncUserExtInfoDAO incUserExtInfoDAO;
    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;
    @Autowired
    private AccountDAO accounDao;
    @Autowired
    private AccountInfoDAO accountInfoDAO;

    /*
     * 检查新注册账号，是否从增量数据库里的账号写入搜狗数据库里
     * 输入"userid，createtim"
     */
    @Test
    public void checkRegDate() {
        List<String> regList = FileIOUtil.readFileByLines("D:\\userid_time_2310.txt");
        String content = null;
        int count = 0;
        for (String str : regList) {
            String[] array = str.split(",");
            String passportId = array[0];
            if (!passportId.contains("@")) {
                passportId = passportId + CommonConstant.SOGOU_SUFFIX;
            }
            try {
                String regTime = array[1];
                Account account = accounDao.getAccountByPassportId(passportId);
                if (account != null) {
                    content = passportId + "," + regTime + "," + account.getRegTime();
                } else {
                    content = passportId + "," + regTime + "," + "账号不存在";
                }
            } catch (Exception e) {
                content = passportId + "日志没有访问时间";

            }
            failedList.add(content);
            count++;
        }
        System.out.println("count:" + count);
        try {
            FileUtil.storeFile("D:\\userid_time_2310_check.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fillRegisterData() {
        List<String> passportIdList = FileIOUtil.readFileByLines("D:\\db\\checkusererror.log.new.2014-06-23");
        String content = null;
        int count = 0;
        for (String passportId : passportIdList) {
            try {
                IncUserInfo incUserInfo = incUserInfoDAO.getIncUserInfo(passportId);
                IncUserOtherInfo incUserOtherInfo = incUserOtherInfoDAO.getIncUserOtherInfo(passportId);
                IncUserExtInfo incUserExtInfo = incUserExtInfoDAO.getIncUserExtInfo(passportId);
                Account account = buildAccount(incUserInfo, incUserOtherInfo, incUserExtInfo);
                if (account != null) {
                    accounDao.insertAccount(passportId, account);
                    AccountInfo accountInfo = buildAccountInfo(passportId, incUserOtherInfo, incUserExtInfo);
                    accountInfoDAO.insertAccountInfo(passportId, accountInfo);
                }
            } catch (Exception e) {
                content = "";
                count++;
                failedList.add(content);
            }

        }
        System.out.println("count:" + count);
        try {
            FileUtil.storeFile("D:\\db\\result.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Account buildAccount(IncUserInfo incUserInfo, IncUserOtherInfo incUserOtherInfo, IncUserExtInfo incUserExtInfo) throws Exception {
        Account account = new Account();
        if (incUserInfo != null) {
            if (!"1".equals(incUserInfo.getFlag())) {
                return null;
            }
            String passportId = incUserInfo.getUserid();
            account.setPassportId(passportId);
            account.setPassword(incUserInfo.getPassword());
            account.setPasswordtype(Integer.parseInt(incUserInfo.getPasswordtype()));
            account.setFlag(AccountStatusEnum.REGULAR.getValue());
            account.setAccountType(AccountTypeEnum.getAccountType(passportId).getValue());
            if (incUserOtherInfo != null) {
                String mobile = incUserOtherInfo.getMobileflag().equals("1") ? incUserOtherInfo.getMobile() : "";
                if (!Strings.isNullOrEmpty(mobile) && PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                    if (!Strings.isNullOrEmpty(mobilePassportMappingDAO.getPassportIdByMobile(mobile))) {
                        mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
                        account.setMobile(incUserOtherInfo.getEmail());
                    }
                }
                String uniqname = incUserOtherInfo.getUniqname();
                if (!Strings.isNullOrEmpty(uniqname)) {
                    if (!Strings.isNullOrEmpty(uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname))) {
                        uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                        account.setUniqname(incUserOtherInfo.getUniqname());
                    }
                }
            }
            if (incUserExtInfo != null) {
                account.setRegTime(incUserExtInfo.getCreatetime());
                account.setRegIp(incUserExtInfo.getCreateip());
            }
            return account;
        } else {
            return null;
        }
    }

    private AccountInfo buildAccountInfo(String passportId, IncUserOtherInfo incUserOtherInfo, IncUserExtInfo incUserExtInfo) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setPassportId(passportId);
        accountInfo.setModifyip(null);
        accountInfo.setUpdateTime(new Date());
        accountInfo.setCreateTime(new Date());
        if (incUserOtherInfo != null) {
            accountInfo.setEmail(incUserOtherInfo.getEmail());
            accountInfo.setProvince(incUserOtherInfo.getProvince());
            accountInfo.setCity(incUserOtherInfo.getCity());
            accountInfo.setPersonalid(incUserOtherInfo.getPersonalid());
        } else if (incUserExtInfo != null) {
            accountInfo.setQuestion(incUserExtInfo.getQuestion());
            accountInfo.setAnswer(incUserExtInfo.getAnswer());
            accountInfo.setBirthday(incUserExtInfo.getBirthday());
            accountInfo.setGender(incUserExtInfo.getGender());
            accountInfo.setFullname(incUserExtInfo.getUsername());
        }
        return accountInfo;
    }
}
