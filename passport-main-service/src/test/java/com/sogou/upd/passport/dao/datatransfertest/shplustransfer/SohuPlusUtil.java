package com.sogou.upd.passport.dao.datatransfertest.shplustransfer;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.datatransfertest.shplustransfer.DO.SohuPassportSidMapping;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-4
 * Time: 上午12:33
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class SohuPlusUtil {

    public static String secret = "59be99d1f5e957ba5a20e8d9b4d76df6";
    public static String appkey = "30000004";
    public static String ip = "127.0.0.1";

    public static SohuPassportSidMapping sendSohuPlusHttp(String passportId) {

        String url = "http://rest.plus.sohuno.com/spassportrest/passport/autoconvert";
        Map<String, String> map = new HashMap();
        map.put("appkey", appkey);
        map.put("passport", passportId);
        map.put("ip", ip);

        SohuPassportSidMapping sohuPassportSidMapping = new SohuPassportSidMapping();
        try {
            Map<String, String> data = sendSpassportSingleHttpReq(url, map);
            // sohuPassportSidMapping = new ObjectMapper().readValue(data, SohuPassportSidMapping.class);
            // BeanUtils.populate(sohuPassportSidMapping, data);
            sohuPassportSidMapping = (SohuPassportSidMapping) toJavaBean(sohuPassportSidMapping, data);

        } catch (Exception e) {
            System.err.println("error parse");
        }
        return sohuPassportSidMapping;
    }

    /**
     * 发送请求至SOHU+，获取结果
     *
     * @param
     * @return
     */
    public static Map sendSpassportSingleHttpReq(String url, Map<String, String> map) {
        RequestModel requestModel = new RequestModel(url);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            requestModel.addParam(entry.getKey(), entry.getValue());
        }
//        requestModel.addParams(map);
        requestModel.addParam("so_sig", computeSigCommon(map));
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);

        Map<String, Map<String, Object>> mapResult = null;
        Map mapData = null;
        try {
            ObjectMapper om = new ObjectMapper();
            mapResult = om.readValue(result, Map.class);// , Map.class);
            mapData = mapResult.get("data");
        } catch (IOException e) {
            System.err.println("error");
        }

        return mapData;
    }

    /**
     * 计算签名
     *
     * @param map
     * @return
     */

    private static String computeSigCommon(Map<String, String> map) {
        Set keys = map.keySet();
        TreeMap<String, String> treeMap = new TreeMap(map);
        StringBuilder sb = new StringBuilder();

        Iterator it = treeMap.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            sb.append(key + "=" + treeMap.get(key));
        }
        sb.append(secret);
        try {
            return Coder.encryptMD5(sb.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将map转换成Javabean
     *
     * @param javabean javaBean
     * @param data     map数据
     */
    public static Object toJavaBean(Object javabean, Map<String, String> data) {
        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("set")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, new Object[]
                            {
                                    data.get(field)
                            });
                }
            } catch (Exception e) {
            }
        }

        return javabean;
    }

    public static void main(String[] args) {
//        String url = "http://rest.plus.sohuno.com/spassportrest/passport/autoconvert";
//        Map<String, String> map = new HashMap();
//        map.put("appkey", appkey);
//        map.put("passport", "3C7882DB1603AC4B60EAACF2CD92BA3F@qq.sohu.com");
//        map.put("ip", ip);
//
//        SohuPassportSidMapping sohuPassportSidMapping = new SohuPassportSidMapping();
//        try {
//            Map<String, String> data = SohuPlusUtil.sendSpassportSingleHttpReq(url, map);
//            sohuPassportSidMapping = (SohuPassportSidMapping) SohuPlusUtil.toJavaBean(sohuPassportSidMapping, data);
//            System.out.println(sohuPassportSidMapping.getSid() + "|" + sohuPassportSidMapping.getSname());
//        } catch (Exception e) {
//            System.err.println("error parse");
//        }

        String url = "http://rest.account.i.sohu.com/account/getpassport/bysid/";
        Map<String, String> map = new HashMap();
        map.put("appkey", "30000004");
        map.put("sids", "1000043573");
//        map.put("ip", ip);

        SohuPassportSidMapping sohuPassportSidMapping = new SohuPassportSidMapping();
        try {
            Map<String, String> data = SohuPlusUtil.sendSpassportSingleHttpReq(url, map);
            sohuPassportSidMapping = (SohuPassportSidMapping) SohuPlusUtil.toJavaBean(sohuPassportSidMapping, data);
            System.out.println(sohuPassportSidMapping.getSid() + "|" + sohuPassportSidMapping.getSname());
        } catch (Exception e) {
            System.err.println("error parse");
        }
    }

}