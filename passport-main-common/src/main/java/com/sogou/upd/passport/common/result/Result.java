package com.sogou.upd.passport.common.result;

import com.sogou.upd.passport.common.utils.ErrorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Service返回值对象
 * User: mayan
 * Date: 13-4-11
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public class Result {

    /**
     * service返回的对象
     */
    private Map<String, Object> data = new HashMap<String, Object>();

    private String status;
    private String statusText;

    public static final String DEFAULT_MODEL_KEY = "value";

    public Result() {
    }

    /**
     * 新增一个带key的返回结果
     *
     * @param key
     * @param obj
     * @return
     */
    public Object addDefaultModel(String key, Object obj) {
        return data.put(key, obj);
    }

    /**
     * 新增一个返回结果
     *
     * @param obj
     * @return
     */
    public Object addDefaultModel(Object obj) {
        return data = (Map) obj;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * 取出整个map对象
     *
     * @return
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 根据错误码返回result对象
     *
     * @param statusText 成功信息描述
     * @param key        成功信息key
     * @param object     封装的数据
     * @return 含错误码及相应的提示信息
     */
    public static Result buildSuccess(String statusText, String key, Object object) {
        Result result = new Result();
        if (object != null) {
            if (object instanceof Map) {
                result.addDefaultModel(object);
            } else {
                result.addDefaultModel(key, object);
            }
        }
        result.setStatus("0");
        result.setStatusText(statusText);
        return result;
    }

    /**
     * 根据错误码返回result对象
     *
     * @param status 或错误码
     * @return 含错误码及相应的提示信息
     */
    public static Result buildError(String status) {
        Result result = new Result();

        result.setStatus(status);
        result.setStatusText(ErrorUtil.getERR_CODE_MSG(status));
        return result;
    }

    /**
     * 根据错误码返回result对象
     *
     * @param status 或错误码
     * @return 含错误码及相应的提示信息
     */
    public static Result buildError(String status, String statusText) {
        Result result = new Result();

        result.setStatus(status);
        result.setStatusText(statusText);
        return result;
    }
}
