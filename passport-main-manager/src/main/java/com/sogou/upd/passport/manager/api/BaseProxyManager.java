package com.sogou.upd.passport.manager.api;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ProxyErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.ManagerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
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

    /**
     * 执行request操作，并将返回结果构造程{@link Result}
     *
     * @param requestModel
     * @return
     */
    protected Result executeResult(final RequestModel requestModel) {
        Result result = new APIResultSupport(false);
        try {
            Map<String, Object> map = this.execute(requestModel);
            if (map.containsKey(SHPPUrlConstant.RESULT_STATUS)) {
                String status = map.get(SHPPUrlConstant.RESULT_STATUS).toString().trim();
                if ("0".equals(status)) {
                    result.setSuccess(true);
                    this.handSHPPMap(map);
                    result.setModels(map);
                } else {
                    Map.Entry<String, String> entry = ProxyErrorUtil.shppErrToSgpp(requestModel.getUrl(), status);
                    result.setCode(entry.getKey());
                    result.setMessage(entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error(requestModel.getUrl() + " execute error ", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION));
        }
        return result;

    }

    protected Map<String, Object> execute(final RequestModel requestModel) {
        if (requestModel == null) {
            throw new IllegalArgumentException("requestModel may not be null");
        }

        //由于SGPP对一些参数的命名和SHPP不一致，在这里做相应的调整
        this.paramNameAdapter(requestModel);

        //设置默认参数同时计算参数的签名
        this.setDefaultParam(requestModel);

        return SGHttpClient.executeBean(requestModel, HttpTransformat.xml, Map.class);
    }

    /**
     * 用于判断和计算默认的code
     * 如果requestModel中已经存在code则不再生成
     *
     * @param requestModel
     */

    private void setDefaultParam(final RequestModel requestModel) {
        //计算默认的codeserverSecret
        String signatureKey=requestModel.getParam("signatureKey").toString();
        String userid = requestModel.getParam(signatureKey).toString();
        long ct = System.currentTimeMillis();
        if (StringUtil.isBlank(userid)) {
            throw new IllegalArgumentException("计算code时"+signatureKey+"不能为空");
        }
        //计算默认的code
        String code = userid + SHPPUrlConstant.APP_ID + SHPPUrlConstant.APP_KEY + ct;
        try {
            code = Coder.encryptMD5(code);
        } catch (Exception e) {
            throw new RuntimeException("calculate default code error", e);
        }
        requestModel.deleteParams("signatureKey");
        requestModel.addParam("code", code);
        requestModel.addParam("ct", ct);
        requestModel.addParam("appid", SHPPUrlConstant.APP_ID);
    }

    /**
     * SGPP对一些参数的命名月SHPP有一些区别，在这里使我们的参数与他们的一致
     *
     * @param requestModel
     */
    private void paramNameAdapter(final RequestModel requestModel) {
        this.paramNameAdapter(requestModel, "client_id", "appid");
        this.paramNameAdapter(requestModel, "passport_id", "userid");
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
    private void handSHPPMap(final Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        map.remove(SHPPUrlConstant.RESULT_STATUS);
        map.remove("uid");
        map.remove("uuid");
        map.remove("uniqname");
    }
}
