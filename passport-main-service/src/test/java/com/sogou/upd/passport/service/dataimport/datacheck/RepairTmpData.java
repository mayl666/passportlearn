package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ProvinceAndCityUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.*;
import com.sogou.upd.passport.model.account.UserExtInfoTmp;
import com.sogou.upd.passport.model.account.UserInfoTmp;
import com.sogou.upd.passport.model.account.UserOtherInfoTmp;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 修复全量数据迁移的临时表
 * User: shipengzhi
 * Date: 14-5-23
 * Time: 上午12:17
 * To change this template use File | Settings | File Templates.
 */
public class RepairTmpData extends BaseTest {

    //从搜狐获取数据失败记录
    private List<String> failedList = Lists.newArrayList();

    @Autowired
    private UserOtherInfoTmpDAO userOtherInfoTmpDAO;
    @Autowired
    private UserExtInfoTmpDAO userExtInfoTmpDAO;
    @Autowired
    private UserInfoTmpDAO userInfoTmpDAO;
    @Autowired
    private AccountInfoDAO accountInfoDAO;
    @Autowired
    private AccountDAO accountDAO;

    private static List<String> errorProviceAndCityList = Lists.newArrayList();

    static {
        errorProviceAndCityList.add("请选择");
        errorProviceAndCityList.add("不限");
        errorProviceAndCityList.add("-1");
    }

    /**
     * 根据userid，调用搜狐API获取用户信息
     */
    @Test
    public void testGetUserInfo() {
        String userid = "lvcunxiao@sogou.com";
        RequestModelXml requestModelXml = FullDataCheckApp.buildRequestModelXml(userid);
        Map<String, Object> shApiResult = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
        System.out.println("result:" + shApiResult);
    }

    /**
     * 修复user_other_info_tmp表里Province、city为中文的情况
     * 输入：userid列表
     * 输出：更新user_other_info_tmp表
     */
    @Test
    public void testRepairtProvinceAndCity() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\all_province_city_error.txt");
        String errorText = "";
        String[] errorProviceAndCity = new String[]{"请选择", "不限", "-1", "请选择城市"};
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            RequestModelXml requestModelXml = FullDataCheckApp.buildRequestModelXml(userid);
            Map<String, Object> shApiResult;
            try {
                shApiResult = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                String status = (String) shApiResult.get("status");
                if (!shApiResult.isEmpty() || !status.equals("0")) {
                    //如果是特定的字符则为空
                    String province = strInArrayToEmpty((String) shApiResult.get("province"), errorProviceAndCity);
                    String city = strInArrayToEmpty((String) shApiResult.get("city"), errorProviceAndCity);
                    //如果是中文则取到响应的代码
                    province = getCodeByChinese(province, ProvinceAndCityUtil.inverseProvinceMap);
                    city = getCodeByChinese(city, ProvinceAndCityUtil.inverseCityMap);
                    UserOtherInfoTmp userOtherInfoTmp = userOtherInfoTmpDAO.getUserOtherInfoTmpByUserid(userid);
                    if (userOtherInfoTmp != null) {
                        int row = userOtherInfoTmpDAO.updateProvinceAndCity(userid, province, city);
                        if (row != 1) {
                            errorText = userid + ":updateQuestion fail!";
                        } else {
                            repairNum++;
                        }
                    } else {
                        errorText = userid + ":getUserOtherInfoTmpByUserid is empty!";
                    }
                } else {
                    errorText = userid + ":request shApi empty!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorText = userid + ":request shApi fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\province_city_error_error.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String strInArrayToEmpty(String str, String[] array) {
        for (String s : array) {
            if (s.equals(str)) {
                return "";
            }
        }
        return str;
    }

    private static String getCodeByChinese(String str, Map<String, String> map) {
        String regex = "^[0-9]*$";
        if (!str.matches(regex)) {
            String code = map.get(str);
            if (Strings.isNullOrEmpty(code)) {
                return str;
            } else {
                return code;
            }
        }
        return str;
    }

    /**
     * 修复user_ext_info_tmp表里Question为中文乱码的情况
     * 输入：userid列表
     * 输出：更新user_ext_info_tmp表
     */
    @Test
    public void testRepairtQuestion() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\repair_user_ext_info_userid.log");
        String errorText = null;
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            RequestModelXml requestModelXml = FullDataCheckApp.buildRequestModelXml(userid);
            Map<String, Object> shApiResult = null;
            try {
                shApiResult = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                String status = (String) shApiResult.get("status");
                if (!shApiResult.isEmpty() || !status.equals("0")) {
                    String question = (String) shApiResult.get("question");
//                    question = new String(question.getBytes("GBK"));
                    // 更新question
                    UserExtInfoTmp userExtInfoTmp = userExtInfoTmpDAO.getUserExtInfoTmpByUserid(userid);
//                    if (userExtInfoTmp != null) {
                    userExtInfoTmp.setQuestion(question);
                    int row = userExtInfoTmpDAO.updateUserExtInfoTmp(userid, userExtInfoTmp);
                    if (row != 1) {
                        errorText = userid + ":updateQuestion fail!";
                    } else {
                        repairNum++;
                    }
//                    } else {
//                        errorText = userid + ":getUserOtherInfoTmpByUserid is empty!";
//                    }
                } else {
                    errorText = userid + ":request shApi empty!";
                }
            } catch (Exception e) {
                errorText = userid + ":request shApi fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\repair_question_userid_error.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查在user_info表，但不在user_other_info表的数据
     */
    @Test
    public void testNoOtherInfo() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\no_other_info");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            RequestModelXml requestModelXml = FullDataCheckApp.buildRequestModelXml(userid);
            Map<String, Object> shApiResult;
            try {
                shApiResult = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                String status = (String) shApiResult.get("status");
                if (!shApiResult.isEmpty() || !status.equals("0")) {
                    String personalid = (String) shApiResult.get("personalid");
                    String mobile = (String) shApiResult.get("personalid");
                    String mobileflag = (String) shApiResult.get("mobileflag");
                    String email = (String) shApiResult.get("email");
                    String emailflag = (String) shApiResult.get("emailflag");
                    String province = (String) shApiResult.get("province");
                    String city = (String) shApiResult.get("city");
                    if (!Strings.isNullOrEmpty(personalid) || !Strings.isNullOrEmpty(mobile) || !Strings.isNullOrEmpty(mobileflag)
                            || !Strings.isNullOrEmpty(email) || !Strings.isNullOrEmpty(emailflag)
                            || !Strings.isNullOrEmpty(province) || !Strings.isNullOrEmpty(city)) {
                        errorText = shApiResult.toString();
                        repairNum++;
                    }
                } else {
                    errorText = userid + ":request shApi empty!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorText = userid + ":request shApi fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\no_other_info_error", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复user_ext_info表中重复数据
     */
    @Test
    public void testRepairUserExtInfoDup() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\user_ext_info_tmp_inc0527wy_userid_dup.csv");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            try {
                String errorUserid = "";
                List<UserExtInfoTmp> userExtInfoTmpList = userExtInfoTmpDAO.listUserExtInfoTmpByUserid(userid);
                UserExtInfoTmp tmp1 = userExtInfoTmpList.get(0);
                int mulNum = userExtInfoTmpList.size();
                if (mulNum != 1) {
                    for (int i = 1; i < mulNum; i++) {
                        UserExtInfoTmp tmp = userExtInfoTmpList.get(i);
                        if (!tmp.equals(tmp1)) {
                            errorText = userid + ":data not same!";
                            errorUserid = userid;
                            break;
                        }
                    }
                    if (Strings.isNullOrEmpty(errorUserid)) {
                        int row = userExtInfoTmpDAO.deleteMulUserExtInfoTmpByUserid(userid, mulNum - 1);
                        if (row != mulNum - 1) {
                            errorText = userid + ":delete muldata error!";
                        }
                    }
                }
            } catch (Exception e) {
                errorText = userid + ":db operation fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\user_ext_info_tmp_inc0527wy_userid_dup_error.csv", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复user_ext_info表中重复数据
     */
    @Test
    public void testRepairUserOtherInfoDup() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\user_other_info_tmp_inc0527wy_userid_dup.csv");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            try {
                String errorUserid = "";
                List<UserOtherInfoTmp> userOtherInfoTmpList = userOtherInfoTmpDAO.listUserOtherInfoTmpByUserid(userid);
                UserOtherInfoTmp tmp1 = userOtherInfoTmpList.get(0);
                int mulNum = userOtherInfoTmpList.size();
                if (mulNum != 1) {
                    for (int i = 1; i < mulNum; i++) {
                        UserOtherInfoTmp tmp = userOtherInfoTmpList.get(i);
                        if (!tmp.equals(tmp1)) {
                            errorText = userid + ":data not same!";
                            errorUserid = userid;
                            break;
                        }
                    }
                    if (Strings.isNullOrEmpty(errorUserid)) {
                        int row = userOtherInfoTmpDAO.deleteMulUserOtherInfoTmpByUserid(userid, mulNum - 1);
                        if (row != mulNum - 1) {
                            errorText = userid + ":delete muldata error!";
                        }
                        total++;
                    }
                }
            } catch (Exception e) {
                errorText = userid + ":db operation fail!";
            }
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\user_other_info_tmp_inc0527wy_userid_dup_error.csv", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复user_info表中重复数据
     */
    @Test
    public void testRepairUserInfoDup() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\user_info_tmp_inc0527wy_userid_dup.csv");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            try {
                String errorUserid = "";
                List<UserInfoTmp> userInfoTmpList = userInfoTmpDAO.listUserInfoTmpByUserid(userid);
                UserInfoTmp tmp1 = userInfoTmpList.get(0);
                int mulNum = userInfoTmpList.size();
                if (mulNum != 1) {
                    for (int i = 1; i < mulNum; i++) {
                        UserInfoTmp tmp = userInfoTmpList.get(i);
                        if (!tmp.equals(tmp1)) {
                            errorText = userid + ":data not same!";
                            errorUserid = userid;
                            break;
                        }
                    }
                    if (Strings.isNullOrEmpty(errorUserid)) {
                        int row = userInfoTmpDAO.deleteMulUserInfoTmpByUserid(userid, mulNum - 1);
                        if (row != mulNum - 1) {
                            errorText = userid + ":delete muldata error!";
                        }
                        repairNum++;
                    }
                }
                total++;
            } catch (Exception e) {
                errorText = userid + ":db operation fail!";
            }
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\user_info_tmp_inc0527wy_userid_dup_error.csv", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复user_ext_info表中重复数据
     */
    @Test
    public void testRepairBirthday() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\account_00_userid.csv");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            try {
                UserExtInfoTmp userExtInfoTmp = userExtInfoTmpDAO.getUserExtInfoTmpByUserid(userid);
                if (userExtInfoTmp == null) {
                    errorText = userid + ":user_ext_info_tmp is empty!";
                } else {
                    String birthday = userExtInfoTmp.getBirthday();
                    if (birthday != null) {
                        int row = accountInfoDAO.updateBirthday(userid, birthday);
                        if (row != 1) {
                            errorText = userid + ":update birthday fail!";
                        }
                        repairNum++;
                    }
                }
            } catch (Exception e) {
                errorText = userid + ":db operation fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\account_00_userid_error.csv", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复account表中regTime字段
     */
    @Test
    public void testErrorRegTime() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\account_regtimeerror_0609.csv");
        String errorText = "";
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            try {
                RequestModelXml requestModelXml = FullDataCheckApp.buildRequestModelXml(userid);
                Map<String, Object> shApiResult = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                String createTime = (String) shApiResult.get("createtime");
                Date regTime;
                if (createTime.equals("0000-00-00 00:00:00")) {
                    regTime = new Date();
                }
                regTime = DateUtil.parse(createTime, DateUtil.DATE_FMT_2);
                int row = accountDAO.updateRegTime(userid, regTime);
                if (row != 1) {
                    errorText = userid + ":update birthday fail!";
                }
                repairNum++;
            } catch (Exception e) {
                errorText = userid + ":db operation fail!";
            }
            total++;
            failedList.add(errorText);
        }
        System.out.println("repairNum:" + repairNum);
        System.out.println("total:" + total);
        try {
            FileUtil.storeFile("D:\\repairDataList\\account_regtimeerror_0609_error.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
