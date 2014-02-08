package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-4
 * Time: 上午12:33
 * To change this template use File | Settings | File Templates.
 */
public class SohuPlusUtil {

    public static String secret = "59be99d1f5e957ba5a20e8d9b4d76df6";
    public static String appkey = "30000004";
    public static String ip = "127.0.0.1";

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
        requestModel.addParam("so_sig", computeSigCommon(map));
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);

        Map<String, Map<String, Object>> mapResult = null;
        Map mapData = null;
        try {
            ObjectMapper om = new ObjectMapper();
            mapResult = om.readValue(result, Map.class);
            mapData = mapResult.get("data");
        } catch (IOException e) {
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

    }

}