package com.sogou.upd.passport.manager.api.repair_write_separate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.FileUtil;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.model.account.Account;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午3:43
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class RepairWriteData extends BaseTest {

    private List<String> contentList = Lists.newArrayList();

    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Autowired
    private AccountDAO accounDao;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    /*
    * 检查账号是否在搜狗数据库里
    * 输入:"userid"
    * 输出:不在搜狗数据库里"userid"
    */
    @Test
    public void checkIsSogouExistDate() {
        List<String> passportList = FileIOUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\phone.txt");
        String content;
        int count = 0;
        String sgPassportId = null;
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        for (String passportId : passportList) {
            if (Strings.isNullOrEmpty(passportId)) {
                continue;
            }
            if (AccountTypeEnum.PHONE == AccountTypeEnum.getAccountType(passportId)) {
                sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(passportId);
            } else {
                Account account = accounDao.getAccountByPassportId(passportId);
                if (account != null) {
                    sgPassportId = account.getPassportId();
                }
            }
            if (Strings.isNullOrEmpty(sgPassportId)) {
                checkUserApiParams.setUserid(passportId);
                Result shResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
                if (!shResult.isSuccess()) {
                    content = passportId + ",shUserId:" + shResult.getModels().get("userid");
                    count++;
                    contentList.add(content);
                }
            }
        }
        content = "total:" + passportList.size() + ",count:" + count;
        contentList.add(content);
        try {
            FileUtil.storeFile("D:\\数据迁移\\写分离前需要迁移的账号\\phone_result.txt", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询手机号绑定的主账号
    @Test
    public void getPassportIdByUsername() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileIOUtil.readFileByLines("D:\\phoneuids.txt");
        String content = null;
        int count = 0;
        for (String data : dataList) {
            String mobile = data;
            if (data.endsWith("@sohu.com")) {
                mobile = data.substring(0, data.lastIndexOf("@sohu.com"));
            }
            String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(sgPassportId)) {
                BaseMoblieApiParams params = new BaseMoblieApiParams();
                params.setMobile(mobile);
                Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                if (shResult.isSuccess()) {
                    String shPassportId = (String) shResult.getModels().get("userid");
                    if (!Strings.isNullOrEmpty(shPassportId) && AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(shPassportId))) {
                        content = mobile + " " + shPassportId;
                    }
                }
            } else {
                continue;
            }
            count++;
            contentList.add(content);
        }
        content = "total:" + dataList.size() + ", sogouNoExist:" + count;
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\passportidlist.txt", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*据来源：双读异常log里用搜狐域账号绑定的手机号来登录的useridld列表，检查是否在搜狗数据库里
    * 输入："mobile userid"
            * 输出：不在搜狗数据库里的账号
    */
    @Test
    public void checkSohuBindMobileDate() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileIOUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\sohubindmobile_0629_0712_total");
        String content = null;
        int count = 0;
        for (String data : dataList) {
            String[] dataArray = data.split(" ");
            String mobile = dataArray[0];
            String passportId = dataArray[1];
            String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(sgPassportId)) {
                content = mobile + "," + passportId + ",sogou no exist";
            } else if (!passportId.equals(sgPassportId)) {
                BaseMoblieApiParams params = new BaseMoblieApiParams();
                params.setMobile(mobile);
                Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                content = mobile + "," + passportId + ",sgPassportId:" + sgPassportId + ",shUserId:" + shResult.getModels().get("userid");
            } else {
                continue;
            }
            count++;
            contentList.add(content);
        }
        content = "total:" + dataList.size() + ", sogouNoExist:" + count;
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\sohubindmobile_0629_0712_total_result", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void checkRegFailDate() {
        List<String> failedList = Lists.newArrayList();
        List<String> passportIdList = FileIOUtil.readFileByLines("D:\\regfail_0716_total");
        String content = null;
        int count = 0;
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        for (String passportId : passportIdList) {
            checkUserApiParams.setUserid(passportId);
            Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
            if (result.isSuccess()) {
                content = passportId + ":" + result.toString();
            }
            failedList.add(content);
            count++;
        }
        System.out.println("count:" + count);
        try {
            FileUtil.storeFile("D:\\regfail_0716_total_result", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
