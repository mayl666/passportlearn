package com.sogou.upd.passport.manager.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXmlGBK;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ProxyErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 封装代理接口的通用方法
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午1:36
 */
@Component
public class BaseProxyManager {

    private static Logger log = LoggerFactory.getLogger(BaseProxyManager.class);

    protected Result executeResult(final RequestModel requestModel) {
        return executeResult(requestModel, null);
    }

    /**
     * 执行request操作，并将返回结果构造程{@link Result}
     *
     * @param requestModel
     * @param signVariableStr 计算code时第一个参数值，如果为null默认是userid
     * @return
     */
    protected Result executeResult(final RequestModel requestModel, String signVariableStr) {
        Result result = new APIResultSupport(false);
        try {
            Map<String, Object> map = this.execute(requestModel, signVariableStr);
            if (map.containsKey(SHPPUrlConstant.RESULT_STATUS)) {
                String status = map.get(SHPPUrlConstant.RESULT_STATUS).toString().trim();
                if ("0".equals(status)) {
                    result.setSuccess(true);
                }
                Map.Entry<String, String> entry = ProxyErrorUtil.shppErrToSgpp(requestModel.getUrl(), status);
                result.setCode(entry.getKey());
                result.setMessage(entry.getValue());
                this.handSHPPMap(map, requestModel.getUrl());   //搜狐Passport接口返回的无用信息删除掉
                result.setModels(map);
            }
        } catch (Exception e) {
            log.warn(requestModel.getUrl() + " execute error ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION));
        }
        return result;

    }

    private Map<String, Object> execute(final RequestModel requestModel, String signVariableStr) {
        if (requestModel == null) {
            throw new IllegalArgumentException("requestModel may not be null");
        }

        //SHPP必须将请求的head中 CONTENT_TYPE 设为 xml
//        requestModel.addHeader(HttpConstant.HeaderType.CONTENT_TYPE, HttpConstant.ContentType.XML_TEXT);

        //由于SGPP对一些参数的命名和SHPP不一致，在这里做相应的调整
        this.paramNameAdapter(requestModel);

        //设置默认参数同时计算参数的签名
        this.setDefaultParam(requestModel, signVariableStr);
        if (requestModel instanceof RequestModelXml || requestModel instanceof RequestModelXmlGBK) {

            Object result_type = requestModel.getParams().get("result_type");
            if (result_type == null) {
                return SGHttpClient.executeBean(requestModel, HttpTransformat.xml, Map.class);
            } else {
                return SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            }
        } else {
            return SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
        }
    }

    /**
     * 用于判断和计算默认的code
     * 如果requestModel中已经存在code则不再生成
     *
     * @param requestModel
     */

    private void setDefaultParam(final RequestModel requestModel, String signVariableStr) {
        //计算默认的codeserverSecret
        if (Strings.isNullOrEmpty(signVariableStr)) {
            signVariableStr = (String) requestModel.getParam("userid");
        }
        if (StringUtil.isBlank(signVariableStr)) {
            throw new IllegalArgumentException("计算code时第一段的字符串不能为空");
        }
        long ct = System.currentTimeMillis();
        //计算默认的code
        String code = ManagerHelper.generatorCodeGBK(signVariableStr, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY, ct);
        requestModel.addParam(SHPPUrlConstant.APPID_STRING, String.valueOf(SHPPUrlConstant.APP_ID));
        requestModel.addParam(CommonConstant.RESQUEST_CODE, code);
        requestModel.addParam(CommonConstant.RESQUEST_CT, String.valueOf(ct));

    }

    /**
     * SGPP对一些参数的命名月SHPP有一些区别，在这里使我们的参数与他们的一致
     *
     * @param requestModel
     */
    protected void paramNameAdapter(final RequestModel requestModel) {
        this.paramNameAdapter(requestModel, "client_id", "appid");
    }

    /**
     * 用于修改参数名称
     *
     * @param requestModel
     * @param oldName
     * @param newName
     */
    protected void paramNameAdapter(final RequestModel requestModel, final String oldName, final String newName) {
        if (requestModel.containsKey(oldName)) {
            Object param = requestModel.getParam(oldName);
            requestModel.deleteParams(oldName);
            requestModel.addParam(newName, param);
        }
    }

    /**
     * SHPP很多接口会返回uid，uuid等，而我们目前没有这样的属性，所以在这里做统一删除
     *
     * @param map
     */
    private void handSHPPMap(final Map<String, Object> map, String url) {
        if (map == null || map.size() == 0) {
            return;
        }
        map.remove(SHPPUrlConstant.RESULT_STATUS);
        map.remove("uid");
        map.remove("uuid");
        //如果是获取用户信息链接，忽略uniqname
        if (!url.equals(SHPPUrlConstant.GET_USER_INFO)) {
            map.remove("uniqname");
        }
        map.remove("errmsg");
    }

    /**
     * 调用搜狐接口获取cookie
     *
     * @param cookieApiParams
     * @return
     */
    protected Result getCookieInfoFromSoHu(CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_COOKIE_VALUE_FROM_SOHU, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(cookieApiParams);
        requestModelXml.getParams().put("result_type", "json");       //sohu 传 xml参数，返回json
        Result getCookieInfoResult = executeResult(requestModelXml);
        if (getCookieInfoResult.isSuccess()) {
            Object obj = getCookieInfoResult.getModels().get("data");
            if (obj != null && obj instanceof List) {
                List<Map<String, String>> listMap = (List<Map<String, String>>) obj;
                if (CollectionUtils.isNotEmpty(listMap)) {
                    for (Map<String, String> map : listMap) {
                        String key = map.get("name");
                        String value = map.get("value");
                        if ("ppinf".equals(key)) {
                            result.getModels().put("ppinf", value);
                        }
                        if ("pprdig".equals(key)) {
                            result.getModels().put("pprdig", value);
                        }
                        if ("passport".equals(key)) {
                            result.getModels().put("passport", value);
                        }
                    }
                }
            }
            result.setSuccess(true);
            result.setMessage("获取cookie成功");
            result.setDefaultModel("userid", cookieApiParams.getUserid());
        } else {
            result = getCookieInfoResult;
        }
        return result;
    }

}
