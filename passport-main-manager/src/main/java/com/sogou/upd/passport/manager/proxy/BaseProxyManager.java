package com.sogou.upd.passport.manager.proxy;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.ManagerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午1:36
 */
@Component
public class BaseProxyManager {

    private static Logger log = LoggerFactory.getLogger(BaseProxyManager.class);

    protected Result executeResult(final RequestModel requestModel) {
        Result result = new APIResultSupport(false);
        Map<String, Object> map = this.execute(requestModel);
        if (map.containsKey(SHPPUrlConstant.RESULT_STATUS)) {
            String status = map.get(SHPPUrlConstant.RESULT_STATUS).toString();
            if ("0".equals(status)) {
                result.setSuccess(true);
            }
            result.setCode(status);
        }
        result.setModels(map);
        return result;
    }

    protected Map<String, Object> execute(final RequestModel requestModel) {
        if (requestModel == null) {
            throw new IllegalArgumentException("requestModel may not be null");
        }

        //由于SGPP对一些参数的命名和SHPP不一致，在这里做相应的调整
        this.paramNameAdapter(requestModel);

        //计算参数的签名
        this.calculateDefaultCode(requestModel);

        return SGHttpClient.executeBean(requestModel, HttpTransformat.xml, Map.class);
    }

    /**
     * 用于判断和计算默认的code
     * 如果requestModel中已经存在code则不再生成
     *
     * @param requestModel
     */
    private void calculateDefaultCode(final RequestModel requestModel) {
        //系统当前时间
        long ct = System.currentTimeMillis();
        String passportId = requestModel.getParam("userid").toString();
        String code = ManagerHelper.generatorCode(passportId, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY, ct);
        requestModel.addParam("code", code);
        requestModel.addParam("ct", ct);
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
    protected void paramNameAdapter(final RequestModel requestModel, String oldName, String newName) {
        if (requestModel.containsKey(oldName)) {
            Object param = requestModel.getParam(oldName);
            requestModel.deleteParams(oldName);
            requestModel.addParam(newName, param);
        }
    }
}
