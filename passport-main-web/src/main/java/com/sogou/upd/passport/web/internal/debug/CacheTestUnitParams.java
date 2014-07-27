package com.sogou.upd.passport.web.internal.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.CacheOperEnum;
import com.sogou.upd.passport.common.parameter.CacheTypeEnum;
import com.sogou.upd.passport.common.utils.ProvinceAndCityUtil;
import com.sogou.upd.passport.common.utils.UniqNameUtil;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-27
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public class CacheTestUnitParams extends BaseApiParams {
    @NotBlank(message = "key不允许为空")
    private String key; // 缓存的key
    @NotBlank(message = "type不允许为空")
    private String type; //缓存类型，有db、cache、token
    @NotBlank(message = "oper不允许为空")
    private String oper; //操作类型，仅支持get、hget、hgetall

    @AssertTrue(message = "不支持的type")
    private boolean isValidType() {
        if (Strings.isNullOrEmpty(type)) {
            return true;
        }
        return CacheTypeEnum.CacheTypeList.contains(type);
    }

    @AssertTrue(message = "不支持的oper")
    private boolean isValidOper() {
        if (Strings.isNullOrEmpty(oper)) {
            return true;
        }
        return CacheOperEnum.CacheOperList.contains(oper);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }
}