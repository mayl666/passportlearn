package com.sogou.upd.passport.web.account.form;

/**
 * Created by hujunfei Date: 14-4-15 Time: 下午12:38
 */
public class SlowInfoParams {
    private String api;     // 请求接口
    private String cost;    // 响应时间
    private String limit;   // 阈值
    private String status;  // 返回的结果码，请求失败则为-1

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
