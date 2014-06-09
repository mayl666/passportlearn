package com.sogou.upd.passport.web.internal.debug;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 数据同步中缓存更新参数类
 * User: shipengzhi
 * Date: 14-6-3
 * Time: 下午8:21
 * To change this template use File | Settings | File Templates.
 */
public class CacheSyncParam {

    @NotBlank(message = "用户id不允许为空")
    private String key; // passport_id 或 mobile
    @Min(0)
    private long ts; //timestamp,单位
    @NotBlank(message = "code不允许为空")
    private String code; //MD5（key+tn+ts+secret）

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
