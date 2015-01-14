package com.sogou.upd.passport.web.internal.connect;

import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.form.BaseUserApiParams;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-5
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/connect")
public class InternalQQOpenAPiController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(InternalQQOpenAPiController.class);
    private static final String QQ_FRIENDS_URL = "http://203.195.155.61:80/internal/qq/friends_info";
    private static final String QQ_FRIENDS_OPENID_URL = "http://203.195.155.61:80/internal/qq/friends_openid";
    private static final String GET_QQ_FRIENDS_URL = "http://203.195.155.61:80/internal/qq/friends_info";
    private static final String GET_QQ_FRIENDS_AES_URL = "http://203.195.155.61:80/internal/qq/friends_aesinfo";

    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";

    //QQ正确返回状态码
    private String QQ_RET_CODE = "0";

    @Autowired
    private ConnectApiManager sgConnectApiManager;

    //    @InterfaceSecurity
    @ResponseBody
    @RequestMapping(value = "/get_friends_info")
    public String get_qqfriends(HttpServletRequest req, BaseUserApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String userId = params.getUserid();
        int clientId = params.getClient_id();
        String third_appid = params.getThird_appid();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            /*if (!isAccessAccept(clientId, req)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }*/
            Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId, third_appid);
            if (!obtainTKeyResult.isSuccess()) {
                return obtainTKeyResult.toString();
            }
            String tKey = (String) obtainTKeyResult.getModels().get("tKey");
            if (StringUtil.isEmpty(tKey)) {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                return result.toString();
            }

            RequestModel requestModel = new RequestModel(GET_QQ_FRIENDS_AES_URL);
            requestModel.addParam("userid", userId);
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            long start = System.currentTimeMillis();
            String returnVal = SGHttpClient.executeStr(requestModel);
            String str = AES.decryptURLSafeString(returnVal, TKEY_SECURE_KEY);
            Map map = JacksonJsonMapperUtil.getMapper().readValue(str, Map.class);
//            String str = SGHttpClient.executeForBigData(requestModel);

//            Map inParammap = new HashMap();
//            inParammap.put("userid", userId);
//            inParammap.put("tKey", tKey);
//            String str = this.send(QQ_FRIENDS_URL,"POST",inParammap,null);
//            Pair<Integer, String> pair = HttpClientUtil.post(QQ_FRIENDS_URL, inParammap);
//            Map map = JacksonJsonMapperUtil.getMapper().readValue(pair.getRight(), Map.class);

            logger.warn("SGHttpClient.executeStr time : " + (System.currentTimeMillis() - start));

            String resp = null;
            if (!CollectionUtils.isEmpty(map)) {
                if (map.containsKey("ret")) {
                    String ret = String.valueOf(map.get("ret"));
                    if (QQ_RET_CODE.equals(ret)) {
                        result.setSuccess(true);
                        if (map.containsKey("items")) {
                            List<Map<String, Object>> list = changePassportId((List<Map<String, Object>>) map.get("items"), third_appid);
                            result.setDefaultModel(list);
                        }
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                }
//                map = changeResult(map, third_appid);
                //调用返回
//                resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
            }
           /* if (Strings.isNullOrEmpty(resp)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }*/


            //构建参数
         /*   Map<String, Collection<String>> paramsMap = Maps.newHashMap();
            paramsMap.put("userid", Lists.newArrayList(userId));
            paramsMap.put("tKey", Lists.newArrayList(tKey));

            AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();
            String responseData = asyncHttpClientService.sendPost(QQ_FRIENDS_URL, paramsMap, null);
            if (Strings.isNullOrEmpty(responseData)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }*/


//            Map<String, String> paramsData = Maps.newHashMap();
//            Map<String, String> headerMap = Maps.newHashMap();
//            paramsData.put("userid", userId);
//            paramsData.put("tKey", tKey);
//            HttpClientService httpClientService = new HttpClientService();
//            String responseData = httpClientService.sendPost(QQ_FRIENDS_URL, paramsData, headerMap);

            /*AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();
            String responseData = asyncHttpClientService.sendPreparePost(QQ_FRIENDS_URL, paramsData);
            if (Strings.isNullOrEmpty(responseData)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }*/

//            result.setModels(changeResult(JacksonJsonMapperUtil.getMapper().readValue(responseData, Map.class)));
//            result.setModels(changeResult(map));
//            result.setDefaultModel("data", pair.getRight());

            return result.toString();
        } catch (Exception e) {
            logger.error("get qq friends error. ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    public Map changeResult(Map map, String third_appid) throws Exception {
        if (map.containsKey("msg")) {
            String msg = String.valueOf(map.get("msg"));
            map.put("statusText", msg);
            map.remove("msg");
        }
        if (map.containsKey("ret")) {
            String ret = String.valueOf(map.get("ret"));
            if (QQ_RET_CODE.equals(ret)) {
                map.put("status", ret);
            } else {
                map.put("status", ErrorUtil.ERR_CODE_CONNECT_FAILED);
            }
            map.remove("ret");
        }
        if (map.containsKey("items")) {
            List<Map<String, Object>> list = changePassportId((List<Map<String, Object>>) map.get("items"), third_appid);
            map.put("data", list);
            map.remove("items");
        }
        if (map.containsKey("is_lost")) {
            map.remove("is_lost");
        }
        return map;
    }


    public List<Map<String, Object>> changePassportId(List<Map<String, Object>> list, String third_appid) {
        if (!CollectionUtils.isEmpty(list)) {
            List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                String openid = String.valueOf(map.get("openid"));
                if (!StringUtils.isNullOrEmpty(openid)) {
                    Result result = sgConnectApiManager.getConnectRelation(openid, 3, third_appid);
                    if (!result.isSuccess()) {
                        logger.error("connectRelation中没有此openid,去除此记录返回，openid : " + openid);
                        removeList.add(map);
                        continue;
                    } else {
                        ConnectRelation connectRelation = (ConnectRelation) result.getModels().get("connectRelation");
                        map.put("userid", connectRelation.getPassportId());
                        map.remove("openid");
                    }
                }
            }
            list.removeAll(removeList);
        }
        return list;
    }


   /* private String send(String urlString, String method,
                        Map<String, String> parameters, Map<String, String> propertys)
            throws IOException {
        HttpURLConnection urlConnection = null;
        Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.129.192.147", 8888));

        if (method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(parameters.get(key));
                i++;
            }
            urlString += param;
        }
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection(proxy);

        urlConnection.setRequestMethod(method);
        urlConnection.setReadTimeout(999999999);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);

        if (propertys != null)
            for (String key : propertys.keySet()) {
                urlConnection.addRequestProperty(key, propertys.get(key));
            }

        if (method.equalsIgnoreCase("POST") && parameters != null) {
            StringBuffer param = new StringBuffer();
            for (String key : parameters.keySet()) {
                param.append("&");
                param.append(key).append("=").append(parameters.get(key));
            }
            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }
        return this.makeContent(urlString, urlConnection);
    }

    *//*
     * 得到响应对象
     *
     * @param urlConnection
     * @return 响应对象
     * @throws IOException
     *//*
    private String makeContent(String urlString,
                               HttpURLConnection urlConnection) throws IOException {
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            return temp.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/get_friends_info/test")
    public String test(HttpServletRequest req, BaseUserApiParams params) throws IOException {
        Result result = new APIResultSupport(false);
        String userId = params.getUserid();
        int clientId = params.getClient_id();
        String third_appid = params.getThird_appid();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            *//*if (!isAccessAccept(clientId, req)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }*//*
            Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId, third_appid);
            if (!obtainTKeyResult.isSuccess()) {
                return obtainTKeyResult.toString();
            }
            String tKey = (String) obtainTKeyResult.getModels().get("tKey");
            if (StringUtil.isEmpty(tKey)) {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                return result.toString();
            }

            RequestModel requestModel = new RequestModel(QQ_FRIENDS_URL);
            requestModel.addParam("userid", userId);
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
//            Map map = SGHttpClient.execute(requestModel, HttpTransformat.json, Map.class);

            String curl = "curl -d \"tKey=" + tKey + "&userid=" + userId + "\" \"http://203.195.155.61:80/internal/qq/friends_info\"";
            Process process = Runtime.getRuntime().exec(curl);
            while (process.waitFor() == 1)
                process.waitFor();
            logger.error(curl);
            InputStream inputStream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader reader1 = new BufferedReader(reader);
            String str = reader1.readLine();
            StringBuffer resultValue = new StringBuffer();
            while (str != null) {
                resultValue.append(str);
                str = reader1.readLine();
            }
            return resultValue.toString();


       *//*     Map inParammap = new HashMap();
            inParammap.put("userid", userId);
            inParammap.put("tKey", tKey);
            String str = this.send(QQ_FRIENDS_URL,"POST",inParammap,null);
            Map map = JacksonJsonMapperUtil.getMapper().readValue(str,Map.class);*//*

          *//*  String resp = null;
            if (!CollectionUtils.isEmpty(map)) {
                map = changeResult(map);
                //调用返回
                resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
            }
            if (Strings.isNullOrEmpty(resp)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }*//*


            //构建参数
*//*            Map<String, List<String>> paramsMap = Maps.newHashMap();
            paramsMap.put("userid", Lists.newArrayList(userId));
            paramsMap.put("tKey", Lists.newArrayList(tKey));

            AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();
            String responseData = asyncHttpClientService.sendPost(QQ_FRIENDS_URL, paramsMap, null);
            if (Strings.isNullOrEmpty(responseData)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }
            return responseData;*//*

//            result.setSuccess(true);
//            result.getModels().put("tKey", tKey);
//            return result.toString();
//            return resp;
        } catch (Exception e) {
            logger.error("get qq friends error. ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/get_friends_info/test1")
    public String test1(HttpServletRequest req, BaseUserApiParams params) throws IOException {
        String test = "{ret=0, msg=, is_lost=0, items=[{openid=C7208AF09F5A15632FC1788F6735908F, nickname=cl茅ment, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/C7208AF09F5A15632FC1788F6735908F/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/C7208AF09F5A15632FC1788F6735908F/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/C7208AF09F5A15632FC1788F6735908F/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/C7208AF09F5A15632FC1788F6735908F/100}, {openid=E0107FA600FD95B5DA4A09F84D67CCAB, nickname=鏋楁檽鏇? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E0107FA600FD95B5DA4A09F84D67CCAB/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E0107FA600FD95B5DA4A09F84D67CCAB/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E0107FA600FD95B5DA4A09F84D67CCAB/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E0107FA600FD95B5DA4A09F84D67CCAB/100}, {openid=78FB25FC97797621379EEE555569AAAC, nickname=闄堝簡鍚? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/78FB25FC97797621379EEE555569AAAC/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/78FB25FC97797621379EEE555569AAAC/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/78FB25FC97797621379EEE555569AAAC/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/78FB25FC97797621379EEE555569AAAC/100}, {openid=D7B9BAB7114E0ABBDBF672D219E62D08, nickname=Stick, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/D7B9BAB7114E0ABBDBF672D219E62D08/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/D7B9BAB7114E0ABBDBF672D219E62D08/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/D7B9BAB7114E0ABBDBF672D219E62D08/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/D7B9BAB7114E0ABBDBF672D219E62D08/100}, {openid=22F417CA584053A636DF9672A38E19C6, nickname=Summer., figureurl=http://qzapp.qlogo.cn/qzapp/100294784/22F417CA584053A636DF9672A38E19C6/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/22F417CA584053A636DF9672A38E19C6/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/22F417CA584053A636DF9672A38E19C6/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/22F417CA584053A636DF9672A38E19C6/100}, {openid=2F14CFE861992635F458AEB67E05E9E9, nickname=Richard, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/2F14CFE861992635F458AEB67E05E9E9/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/2F14CFE861992635F458AEB67E05E9E9/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/2F14CFE861992635F458AEB67E05E9E9/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/2F14CFE861992635F458AEB67E05E9E9/100}, {openid=E6D6AD907DB9D8FF1A3AA8B748DBE2DC, nickname=鏌犳閰歌尪, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E6D6AD907DB9D8FF1A3AA8B748DBE2DC/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E6D6AD907DB9D8FF1A3AA8B748DBE2DC/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E6D6AD907DB9D8FF1A3AA8B748DBE2DC/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E6D6AD907DB9D8FF1A3AA8B748DBE2DC/100}, {openid=7D9B80BF26383CFDFC520AAE6C60349D, nickname=瀹堟姢鑰? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/7D9B80BF26383CFDFC520AAE6C60349D/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/7D9B80BF26383CFDFC520AAE6C60349D/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/7D9B80BF26383CFDFC520AAE6C60349D/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/7D9B80BF26383CFDFC520AAE6C60349D/100}, {openid=B6E06476ED05D22E72795662F228ADCE, nickname=        Cora, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/B6E06476ED05D22E72795662F228ADCE/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/B6E06476ED05D22E72795662F228ADCE/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/B6E06476ED05D22E72795662F228ADCE/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/B6E06476ED05D22E72795662F228ADCE/100}, {openid=6AC2568B1E1CA543D349657045AD3E8C, nickname=娴烽, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/6AC2568B1E1CA543D349657045AD3E8C/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/6AC2568B1E1CA543D349657045AD3E8C/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/6AC2568B1E1CA543D349657045AD3E8C/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/6AC2568B1E1CA543D349657045AD3E8C/100}, {openid=161163647056990B1CDAE999A8DDD287, nickname=Zr, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/161163647056990B1CDAE999A8DDD287/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/161163647056990B1CDAE999A8DDD287/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/161163647056990B1CDAE999A8DDD287/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/161163647056990B1CDAE999A8DDD287/100}, {openid=8368875312106BABC86C2C1E494AAE47, nickname=闈欏惉椋庡惣, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/8368875312106BABC86C2C1E494AAE47/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/8368875312106BABC86C2C1E494AAE47/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/8368875312106BABC86C2C1E494AAE47/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/8368875312106BABC86C2C1E494AAE47/100}, {openid=0D361E50CE769207814C17799B565873, nickname=Flying, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/0D361E50CE769207814C17799B565873/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/0D361E50CE769207814C17799B565873/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/0D361E50CE769207814C17799B565873/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/0D361E50CE769207814C17799B565873/100}, {openid=E777EDF45C7D1F170755441ED180F0AD, nickname= o(鈭鈭?, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E777EDF45C7D1F170755441ED180F0AD/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E777EDF45C7D1F170755441ED180F0AD/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E777EDF45C7D1F170755441ED180F0AD/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E777EDF45C7D1F170755441ED180F0AD/100}, {openid=A32454B304AB70613C96B87EB65FCFC9, nickname=钃濊壊澶╃┖, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/A32454B304AB70613C96B87EB65FCFC9/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/A32454B304AB70613C96B87EB65FCFC9/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/A32454B304AB70613C96B87EB65FCFC9/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/A32454B304AB70613C96B87EB65FCFC9/100}, {openid=65E3A462F2EE19F701127D8795C398B7, nickname=Nothin锛?G, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/65E3A462F2EE19F701127D8795C398B7/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/65E3A462F2EE19F701127D8795C398B7/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/65E3A462F2EE19F701127D8795C398B7/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/65E3A462F2EE19F701127D8795C398B7/100}, {openid=D6BDDEDDA8A9C09C7B22A7D7140CC167, nickname=璺冲垁鐨勫厰瀛? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/D6BDDEDDA8A9C09C7B22A7D7140CC167/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/D6BDDEDDA8A9C09C7B22A7D7140CC167/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/D6BDDEDDA8A9C09C7B22A7D7140CC167/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/D6BDDEDDA8A9C09C7B22A7D7140CC167/100}, {openid=48B61416766538316816F0F1DAAA9F4D, nickname=warlock, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/48B61416766538316816F0F1DAAA9F4D/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/48B61416766538316816F0F1DAAA9F4D/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/48B61416766538316816F0F1DAAA9F4D/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/48B61416766538316816F0F1DAAA9F4D/100}, {openid=837D70707C21E87BB6D9550E7DC27321, nickname=Aan, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/837D70707C21E87BB6D9550E7DC27321/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/837D70707C21E87BB6D9550E7DC27321/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/837D70707C21E87BB6D9550E7DC27321/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/837D70707C21E87BB6D9550E7DC27321/100}, {openid=CF273E3E035DACDF1CFF4AF5816085A1, nickname=銆愶紗绁烇紗銆? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/CF273E3E035DACDF1CFF4AF5816085A1/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/CF273E3E035DACDF1CFF4AF5816085A1/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/CF273E3E035DACDF1CFF4AF5816085A1/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/CF273E3E035DACDF1CFF4AF5816085A1/100}, {openid=C845B130105FEC5ABCB15DB832ADED50, nickname=鐞﹀绁楁剾浼便儫, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/C845B130105FEC5ABCB15DB832ADED50/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/C845B130105FEC5ABCB15DB832ADED50/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/C845B130105FEC5ABCB15DB832ADED50/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/C845B130105FEC5ABCB15DB832ADED50/100}, {openid=47FB48A1586E94C28300525D7395568D, nickname=蹇嗛毃棰? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/47FB48A1586E94C28300525D7395568D/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/47FB48A1586E94C28300525D7395568D/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/47FB48A1586E94C28300525D7395568D/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/47FB48A1586E94C28300525D7395568D/100}, {openid=67C30AD7C748B5078D7D89A664EC0FAE, nickname=Lee, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/67C30AD7C748B5078D7D89A664EC0FAE/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/67C30AD7C748B5078D7D89A664EC0FAE/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/67C30AD7C748B5078D7D89A664EC0FAE/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/67C30AD7C748B5078D7D89A664EC0FAE/100}, {openid=2DFA063F1B884CD9B053C293D499FC3B, nickname=Allen Sun, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/2DFA063F1B884CD9B053C293D499FC3B/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/2DFA063F1B884CD9B053C293D499FC3B/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/2DFA063F1B884CD9B053C293D499FC3B/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/2DFA063F1B884CD9B053C293D499FC3B/100}, {openid=76A5AE47450A36DC8743C895432D4E70, nickname=  鏉扮勘涓嶉€婄殑鍗庝附, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/76A5AE47450A36DC8743C895432D4E70/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/76A5AE47450A36DC8743C895432D4E70/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/76A5AE47450A36DC8743C895432D4E70/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/76A5AE47450A36DC8743C895432D4E70/100}, {openid=3F9F7561BEB311D3C43D043159779970, nickname=鐨囬樋鐜? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/3F9F7561BEB311D3C43D043159779970/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/3F9F7561BEB311D3C43D043159779970/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/3F9F7561BEB311D3C43D043159779970/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/3F9F7561BEB311D3C43D043159779970/100}, {openid=E7B710B990623B3925358634FD3322BD, nickname=銆丒nd 銉幝癝, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E7B710B990623B3925358634FD3322BD/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E7B710B990623B3925358634FD3322BD/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E7B710B990623B3925358634FD3322BD/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E7B710B990623B3925358634FD3322BD/100}, {openid=2A0D074AF7EE8240851503A305FDA190, nickname=kitty鐖? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/2A0D074AF7EE8240851503A305FDA190/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/2A0D074AF7EE8240851503A305FDA190/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/2A0D074AF7EE8240851503A305FDA190/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/2A0D074AF7EE8240851503A305FDA190/100}, {openid=F8FC2490A062F2186BB12C8CCDEC4522, nickname=Andy, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/F8FC2490A062F2186BB12C8CCDEC4522/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/F8FC2490A062F2186BB12C8CCDEC4522/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/F8FC2490A062F2186BB12C8CCDEC4522/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/F8FC2490A062F2186BB12C8CCDEC4522/100}, {openid=056B15F99925016562B24E2070AE7AF5, nickname=Clover闄堢煶, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/056B15F99925016562B24E2070AE7AF5/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/056B15F99925016562B24E2070AE7AF5/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/056B15F99925016562B24E2070AE7AF5/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/056B15F99925016562B24E2070AE7AF5/100}, {openid=B285E7BC2645CCB3CA12DADB1CD143CB, nickname=瀵? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/B285E7BC2645CCB3CA12DADB1CD143CB/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/B285E7BC2645CCB3CA12DADB1CD143CB/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/B285E7BC2645CCB3CA12DADB1CD143CB/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/B285E7BC2645CCB3CA12DADB1CD143CB/100}, {openid=E487CA1BB6F59D5B9832A496FF9BBEE7, nickname=Lane, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E487CA1BB6F59D5B9832A496FF9BBEE7/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E487CA1BB6F59D5B9832A496FF9BBEE7/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E487CA1BB6F59D5B9832A496FF9BBEE7/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E487CA1BB6F59D5B9832A496FF9BBEE7/100}, {openid=B873BA22B240BA297E66BA7862F0F1D0, nickname=鏃犺█, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/B873BA22B240BA297E66BA7862F0F1D0/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/B873BA22B240BA297E66BA7862F0F1D0/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/B873BA22B240BA297E66BA7862F0F1D0/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/B873BA22B240BA297E66BA7862F0F1D0/100}, {openid=57AAE3D444BC6B60024684D94C4959B8, nickname=RAY, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/57AAE3D444BC6B60024684D94C4959B8/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/57AAE3D444BC6B60024684D94C4959B8/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/57AAE3D444BC6B60024684D94C4959B8/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/57AAE3D444BC6B60024684D94C4959B8/100}, {openid=D652F32534E32D7BB36DA2CC5F7EF6E9, nickname=LockHart, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/D652F32534E32D7BB36DA2CC5F7EF6E9/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/D652F32534E32D7BB36DA2CC5F7EF6E9/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/D652F32534E32D7BB36DA2CC5F7EF6E9/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/D652F32534E32D7BB36DA2CC5F7EF6E9/100}, {openid=27D62B681ECC28D09203250452A0D7EE, nickname=褰? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/27D62B681ECC28D09203250452A0D7EE/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/27D62B681ECC28D09203250452A0D7EE/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/27D62B681ECC28D09203250452A0D7EE/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/27D62B681ECC28D09203250452A0D7EE/100}, {openid=7FEE6342BF92B72FE6E893C368C142F7, nickname=鏈漼oung, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/7FEE6342BF92B72FE6E893C368C142F7/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/7FEE6342BF92B72FE6E893C368C142F7/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/7FEE6342BF92B72FE6E893C368C142F7/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/7FEE6342BF92B72FE6E893C368C142F7/100}, {openid=8B89E28F922636FC2CB2FAB9FBD2EAFA, nickname=棰ㄩ櫟淇犲, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/8B89E28F922636FC2CB2FAB9FBD2EAFA/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/8B89E28F922636FC2CB2FAB9FBD2EAFA/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/8B89E28F922636FC2CB2FAB9FBD2EAFA/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/8B89E28F922636FC2CB2FAB9FBD2EAFA/100}, {openid=CC443A927D5416BF9D414FFB224FFF27, nickname=Vinson Fan, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/CC443A927D5416BF9D414FFB224FFF27/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/CC443A927D5416BF9D414FFB224FFF27/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/CC443A927D5416BF9D414FFB224FFF27/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/CC443A927D5416BF9D414FFB224FFF27/100}, {openid=ECDBC90EAD0D771A40F950D3851861D8, nickname=娉涜叮瀹? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/ECDBC90EAD0D771A40F950D3851861D8/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/ECDBC90EAD0D771A40F950D3851861D8/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/ECDBC90EAD0D771A40F950D3851861D8/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/ECDBC90EAD0D771A40F950D3851861D8/100}, {openid=37ACFA44CCAA09D0282F835C124F40EE, nickname=妗滆姳鍒濋煶, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/37ACFA44CCAA09D0282F835C124F40EE/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/37ACFA44CCAA09D0282F835C124F40EE/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/37ACFA44CCAA09D0282F835C124F40EE/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/37ACFA44CCAA09D0282F835C124F40EE/100}, {openid=21CC1DBF786366C3393EE8DE3DA77711, nickname=James Rodr铆guez, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/21CC1DBF786366C3393EE8DE3DA77711/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/21CC1DBF786366C3393EE8DE3DA77711/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/21CC1DBF786366C3393EE8DE3DA77711/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/21CC1DBF786366C3393EE8DE3DA77711/100}, {openid=24B227E677F908251636422CB8D305D7, nickname=Quanhe, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/24B227E677F908251636422CB8D305D7/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/24B227E677F908251636422CB8D305D7/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/24B227E677F908251636422CB8D305D7/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/24B227E677F908251636422CB8D305D7/100}, {openid=3389970F47FD31E5E32833C248042E76, nickname=Kobe, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/3389970F47FD31E5E32833C248042E76/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/3389970F47FD31E5E32833C248042E76/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/3389970F47FD31E5E32833C248042E76/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/3389970F47FD31E5E32833C248042E76/100}, {openid=EFF56046F563FF6402C5D575E6CCECB6, nickname=绛戞ⅵ鑰? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/EFF56046F563FF6402C5D575E6CCECB6/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/EFF56046F563FF6402C5D575E6CCECB6/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/EFF56046F563FF6402C5D575E6CCECB6/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/EFF56046F563FF6402C5D575E6CCECB6/100}, {openid=EB46206D9D73E41EFA8808EDB2949D34, nickname=闃胯彔钀? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/EB46206D9D73E41EFA8808EDB2949D34/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/EB46206D9D73E41EFA8808EDB2949D34/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/EB46206D9D73E41EFA8808EDB2949D34/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/EB46206D9D73E41EFA8808EDB2949D34/100}, {openid=4CE4118F2603225C6516DDF6CA9C3C0E, nickname=鏈夊織鑰呬簨闈欐垚, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/4CE4118F2603225C6516DDF6CA9C3C0E/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/4CE4118F2603225C6516DDF6CA9C3C0E/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/4CE4118F2603225C6516DDF6CA9C3C0E/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/4CE4118F2603225C6516DDF6CA9C3C0E/100}, {openid=E8BB5AF5DF09F90B9FE17046A8207726, nickname=灏忛緳, figureurl=http://qzapp.qlogo.cn/qzapp/100294784/E8BB5AF5DF09F90B9FE17046A8207726/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/E8BB5AF5DF09F90B9FE17046A8207726/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/E8BB5AF5DF09F90B9FE17046A8207726/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/E8BB5AF5DF09F90B9FE17046A8207726/100}, {openid=34CCBDDFC738BF4E07C1302AFDEACE6F, nickname=骞肩鍥? figureurl=http://qzapp.qlogo.cn/qzapp/100294784/34CCBDDFC738BF4E07C1302AFDEACE6F/30, figureurl_1=http://qzapp.qlogo.cn/qzapp/100294784/34CCBDDFC738BF4E07C1302AFDEACE6F/50, figureurl_2=http://qzapp.qlogo.cn/qzapp/100294784/34CCBDDFC738BF4E07C1302AFDEACE6F/100, figureurl_qq=http://q.qlogo.cn/qqapp/100294784/34CCBDDFC738BF4E07C1302AFDEACE6F/100}]}";
        return test;
    }

    @ResponseBody
    @RequestMapping(value = "/get_friends_info/test2")
    public String test2(HttpServletRequest req, BaseUserApiParams params) throws IOException {
        RequestModel requestModel = new RequestModel("http://10.136.24.127:8090/internal/connect/get_friends_info/test1");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
//        Map map = SGHttpClient.execute(requestModel, HttpTransformat.json, Map.class);
        String str = SGHttpClient.executeStrByByte(requestModel);
        return str;
    }


    @ResponseBody
    @RequestMapping(value = "/get_friends_openid")
    public String get_qqfriendsByOpenid(HttpServletRequest req, BaseUserApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String userId = params.getUserid();
        int clientId = params.getClient_id();
        String third_appid = params.getThird_appid();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            *//*if (!isAccessAccept(clientId, req)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }*//*
            Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId, third_appid);
            if (!obtainTKeyResult.isSuccess()) {
                return obtainTKeyResult.toString();
            }
            String tKey = (String) obtainTKeyResult.getModels().get("tKey");
            if (StringUtil.isEmpty(tKey)) {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                return result.toString();
            }

            RequestModel requestModel = new RequestModel(QQ_FRIENDS_OPENID_URL);
            requestModel.addParam("userid", userId);
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            long start = System.currentTimeMillis();
//            Map map = SGHttpClient.execute(requestModel, HttpTransformat.json, Map.class);
//            String str = SGHttpClient.executeForBigData(requestModel);

            Map inParammap = new HashMap();
            inParammap.put("userid", userId);
            inParammap.put("tKey", tKey);
//            String str = this.send(QQ_FRIENDS_URL,"POST",inParammap,null);
            Pair<Integer, String> pair = HttpClientUtil.post(QQ_FRIENDS_OPENID_URL, inParammap);
//            Map map = JacksonJsonMapperUtil.getMapper().readValue(pair.getRight(), Map.class);

            logger.error("SGHttpClient.executeForBigData(OpenID) : " + (System.currentTimeMillis() - start));

            result.setDefaultModel("data", pair.getRight());
            result.setSuccess(true);
            return result.toString();
        } catch (Exception e) {
            logger.error("get qq friends error. ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }



   *//* @ResponseBody
    @RequestMapping(value = "/get_friends_info_openapi")
    public String get_qqfriendsByOpenAPI(HttpServletRequest req, BaseUserApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String userId = params.getUserid();
        int clientId = params.getClient_id();
        String third_appid = params.getThird_appid();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            Result connectTokenResult =  sgConnectApiManager.obtainConnectToken(userId,clientId,third_appid);
            if (!connectTokenResult.isSuccess()) {
                return connectTokenResult.toString();
            }

            ConnectToken connectToken = (ConnectToken) connectTokenResult.getModels().get("connectToken");

            RequestModel requestModel = new RequestModel(QQ_FRIENDS_OPENID_URL);
            requestModel.addParam("openid", connectToken.getOpenid());
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            long start = System.currentTimeMillis();

            Map inParammap = new HashMap();
            inParammap.put("userid", userId);
            inParammap.put("tKey", tKey);
            Pair<Integer, String> pair = HttpClientUtil.post(QQ_FRIENDS_OPENID_URL, inParammap);

            logger.error("SGHttpClient.executeForBigData(OpenID) : " + (System.currentTimeMillis() - start));

            result.setDefaultModel("data", pair.getRight());
            result.setSuccess(true);
            return result.toString();
        } catch (Exception e) {
            logger.error("get qq friends error. ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }*//*
*/
}
