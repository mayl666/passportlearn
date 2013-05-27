package com.sogou.upd.passport.result;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.result.resourcebundle.ResourceBundle;
import com.sogou.upd.passport.result.utils.MessageUtil;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * 从resource bundle中取得result code相关的map的工具类。
 * User: shipengzhi
 * Date: 13-5-27
 * Time: 下午3:01
 */
public class ResultCodeMap {

    private ResultCode resultCode;
    private ResourceBundle resourceBundle;

    /**
     * 创建一个result code map。
     *
     * @param resultCode     结果代码
     * @param resourceBundle 包含resultCode map
     */
    public ResultCodeMap(ResultCode resultCode, ResourceBundle resourceBundle) {
        this.resultCode = resultCode;
        this.resourceBundle = resourceBundle;

        if (resultCode == null) {
            throw new IllegalArgumentException("ResultCode is null");
        }
    }

    /**
     * 取得map所表达的<code>ResultCode</code>名称。
     *
     * @return <code>ResultCode</code>名称
     */
    public String getName() {
        return resultCode.getName();
    }

    /**
     * 取得map所表达的<code>ResultCode</code>值。
     *
     * @return <code>ResultCode</code>值
     */
    public ResultCode getResultCode() {
        return resultCode;
    }

    /**
     * 取得Map
     *
     * @return
     */
    public Map getMap() {
        if ((resourceBundle == null) || (resultCode.getName() == null)) {
            return Maps.newHashMap();
        }
        try {
            Map map = resourceBundle.getMap(resultCode.getName());

            return map;
        } catch (MissingResourceException e) {
            return Maps.newHashMap();
        }
    }

    public String getMessageID() {
        Set keys = getMap().keySet();
        for (Object key : keys) {
            return (String) key;
        }
        return "";
    }

    public String getMessageData() {
        return (String) getMap().get(getMessageID());
    }

}
