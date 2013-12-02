package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.ProxyErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-28
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyQQLightOpenApiManager")
public class ProxyQQLightOpenApiManagerImpl extends BaseProxyManager implements QQLightOpenApiManager {

    /**
     * 调用sohu接口获取用户的openid和accessToken等信息
     *
     * @param baseOpenApiParams
     * @return
     */
    @Override
    public Result getProxyConnectUserInfo(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        Result result = new APIResultSupport(false);
        //如果是post请求，原方法
        RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.GET_CONNECT_QQ_LIGHT_USER_INFO_TEST);
        requestModelJSON.addParams(baseOpenApiParams);
        requestModelJSON.deleteParams(CommonConstant.CLIENT_ID);
        this.setDefaultParams(requestModelJSON, baseOpenApiParams.getUserid(), String.valueOf(clientId), clientKey);
        Map map = SGHttpClient.executeBean(requestModelJSON, HttpTransformat.json, Map.class);
        if (map.containsKey(SHPPUrlConstant.RESULT_STATUS)) {
            String status = map.get(SHPPUrlConstant.RESULT_STATUS).toString().trim();
            if ("0".equals(status)) {
                result.setSuccess(true);
            }
            Map.Entry<String, String> entry = ProxyErrorUtil.shppErrToSgpp(requestModelJSON.getUrl(), status);
            result.setCode(entry.getKey());
            result.setMessage(entry.getValue());
            result.setModels(map);
        }
        return result;
    }

    public RequestModelJSON setDefaultParams(RequestModelJSON requestModelJSON, String userId, String clientId, String clientKey) {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userId, Integer.parseInt(clientId), clientKey, ct);
        requestModelJSON.addParam(SHPPUrlConstant.APPID_STRING, clientId);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CODE, code);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CT, String.valueOf(ct));
        return requestModelJSON;
    }

    /**
     * 调用QQ第三方点亮接口
     *
     * @param sdk                  QQ  SDK参数类
     * @param openid               用户的openid
     * @param openkey              用户的openkey
     * @param qqLightOpenApiParams 代理接口参数类
     * @return
     */
    @Override
    public String executeQQOpenApi(OpenApiV3 sdk, String openid, String openkey, QQLightOpenApiParams qqLightOpenApiParams) {
        // 指定OpenApi Cgi名字
        String scriptName = qqLightOpenApiParams.getOpenApiName();
        // 指定HTTP请求协议类型,目前代理接口走的都是HTTP请求，所以需要sig签名，如果为HTTPS请求，则不需要sig签名
        String protocol = CommonConstant.HTTP;
        // 填充URL请求参数,用来生成sig签名
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("openid", openid);
        params.put("openkey", openkey);
        ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
        HashMap<String, String> maps = null;
        try {
            maps = objectMapper.readValue(qqLightOpenApiParams.getParams().toString(), HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> commonKeySet = maps.keySet();
        for (String dataKey : commonKeySet) {
            params.put(dataKey, maps.get(dataKey));
        }
        //目前QQ SDK只提供了post请求，且已经与QQ确认过，他们目前所有的开放接口post请求都可以正确访问
        String method = CommonConstant.CONNECT_METHOD_POST;
        String resp = null;
        try {
            resp = sdk.api(scriptName, params, protocol, method);
            System.out.println(resp);
        } catch (OpensnsException e) {
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
        }
        return resp;
    }
}
