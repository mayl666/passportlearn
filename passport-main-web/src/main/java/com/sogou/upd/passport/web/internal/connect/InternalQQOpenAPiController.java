package com.sogou.upd.passport.web.internal.connect;

import com.google.common.base.Strings;
import com.rabbitmq.tools.json.JSONUtil;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.form.BaseUserApiParams;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-5
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/connect/qq")
public class InternalQQOpenAPiController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(InternalQQOpenAPiController.class);
    private static final String QQ_FRIENDS_URL = "http://203.195.155.61:80/internal/qq/friends_info";

    //QQ正确返回状态码
    private String QQ_RET_CODE = "0";

    @Autowired
    private ConnectApiManager sgConnectApiManager;

    //    @InterfaceSecurity
    @ResponseBody
    @RequestMapping(value = "/get_qqfriends")
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

            RequestModel requestModel = new RequestModel(QQ_FRIENDS_URL);
            requestModel.addParam("userid", userId);
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            /*Map inParammap = new HashMap();
            inParammap.put("userid",userId);
            inParammap.put("tKey",tKey);*/
            logger.error("start to send http request get the qq friends");
            Map map = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
//            String str = this.send(QQ_FRIENDS_URL,"POST",inParammap,null);
            logger.error(map.toString());
            logger.error("end to send http request get the qq friends");
            String resp = null;
            if (map != null && map.size() > 0) {
                map = changeResult(map);
                //调用返回
                resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
            }
            if (Strings.isNullOrEmpty(resp)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }


            //构建参数
/*            Map<String, List<String>> paramsMap = Maps.newHashMap();
            paramsMap.put("userid", Lists.newArrayList(userId));
            paramsMap.put("tKey", Lists.newArrayList(tKey));

            AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();
            String responseData = asyncHttpClientService.sendPost(QQ_FRIENDS_URL, paramsMap, null);
            if (Strings.isNullOrEmpty(responseData)) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                return result.toString();
            }
            return responseData;*/

//            result.setSuccess(true);
//            result.getModels().put("tKey", tKey);
//            return result.toString();
            return resp;
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

    public Map changeResult(Map map) throws IOException {
        if (!CollectionUtils.isEmpty(map) && map.containsKey("msg")) {
            String msg = String.valueOf(map.get("msg"));
            map.put("statusText", msg);
            map.remove("msg");
        }
        if (!CollectionUtils.isEmpty(map) && map.containsKey("ret")) {
            String ret = String.valueOf(map.get("ret"));
            if (QQ_RET_CODE.equals(ret)) {
                map.put("status", ret);
            } else {
                map.put("status", ErrorUtil.ERR_CODE_CONNECT_FAILED);
            }
            map.remove("ret");
        }
        if (!CollectionUtils.isEmpty(map) && map.containsKey("items")) {
            map.put("data", map.get("items"));
            map.remove("items");
        }
        if (!CollectionUtils.isEmpty(map) && map.containsKey("is_lost")) {
            map.remove("is_lost");
        }
        return map;
    }



    /*private String  send(String urlString, String method,
                             Map<String, String> parameters, Map<String, String> propertys)
            throws IOException {
        HttpURLConnection urlConnection = null;

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
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(method);
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

    *//**
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
    }*/
}
