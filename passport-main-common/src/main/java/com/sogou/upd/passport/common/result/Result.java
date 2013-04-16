package com.sogou.upd.passport.common.result;

import net.sf.json.JSONObject;

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
        return data.put(DEFAULT_MODEL_KEY, obj);
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


    public String toJson(Result result){
        JSONObject jsonObject = JSONObject.fromObject(result);
        return jsonObject.toString();
    }

    public static void main(String[] args) {
        Result result1 = new Result();

//        result1.addDefaultModel("smscode","65652") ;
        result1.setStatus("1000");
        result1.setStatusText("获取注册验证码成功");


//        result1.addDefaultModel();


        JSONObject jsonObject = JSONObject.fromObject(result1);

        System.out.println(jsonObject.toString());
    }

}
