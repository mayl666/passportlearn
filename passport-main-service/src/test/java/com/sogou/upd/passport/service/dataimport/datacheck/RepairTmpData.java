package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.ProvinceAndCityUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.UserExtInfoTmpDAO;
import com.sogou.upd.passport.dao.account.UserOtherInfoTmpBakDAO;
import com.sogou.upd.passport.dao.account.UserOtherInfoTmpDAO;
import com.sogou.upd.passport.model.account.UserExtInfoTmp;
import com.sogou.upd.passport.model.account.UserOtherInfoTmp;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    private UserOtherInfoTmpBakDAO userOtherInfoTmpBakDAO;
    @Autowired
    private UserExtInfoTmpDAO userExtInfoTmpDAO;

    private static List<String> errorProviceAndCityList = Lists.newArrayList();

    static {
        errorProviceAndCityList.add("请选择");
        errorProviceAndCityList.add("不限");
        errorProviceAndCityList.add("-1");
    }

    /**
     * 修复user_other_info_tmp表里Province、city为中文的情况
     * 输入：userid列表
     * 输出：更新user_other_info_tmp表
     */
    @Test
    public void testRepairtProvinceAndCity() {
        List<String> useridList = FileIOUtil.readFileByLines("D:\\repairDataList\\province_city_error.csv");
        String errorText = "";
        String[] errorProviceAndCity = new String[]{"请选择", "不限", "-1"};
        int total = 0;
        int repairNum = 0;
        for (String userid : useridList) {
            RequestModelXml requestModelXml = FullDataCheckApp.bulidRequestModelXml(userid);
            Map<String, Object> shApiResult = null;
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
                    UserOtherInfoTmp userOtherInfoTmp = userOtherInfoTmpBakDAO.getUserOtherInfoTmpByUserid(userid);
                    if(userOtherInfoTmp != null){
                        userOtherInfoTmp.setProvince(province);
                        userOtherInfoTmp.setCity(city);
                        int row = userOtherInfoTmpBakDAO.updateUserOtherInfoTmp(userid, userOtherInfoTmp);
                        if (row != 1) {
                            errorText = userid + ":updateQuestion fail!";
                        } else {
                            repairNum++;
                        }
                    }   else {
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
            if (!s.equals(str)) {
                return str;
            } else {
                return "";
            }
        }
        return "";
    }

    private static String getCodeByChinese(String str, Map<String, String> map) {
        String regex = "^[0-9]*$";
        if(!str.matches(regex)){
            String code = map.get(str);
            if(Strings.isNullOrEmpty(code)){
                return str;
            }  else{
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
            RequestModelXml requestModelXml = FullDataCheckApp.bulidRequestModelXml(userid);
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
}
