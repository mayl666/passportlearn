package com.sogou.upd.passport.result;

import com.sogou.upd.passport.result.utils.MessageUtil;

import java.util.ResourceBundle;

/**
 * 从resource bundle中取得result code相关的message的工具类。
 *
 * User: shipengzhi
 * Date: 13-5-24
 * Time: 上午12:21
 */
public class ResultCodeMessage {
    private ResultCode     resultCode;
    private ResourceBundle resourceBundle;
    private Object[]       params;

    /**
     * 创建一个result code message。
     *
     * @param resultCode 结果代码
     * @param resourceBundle 包含消息字符串的
     */
    public ResultCodeMessage(ResultCode resultCode, ResourceBundle resourceBundle) {
        this(resultCode, resourceBundle, null);
    }

    /**
     * 创建一个result code message。
     *
     * @param resultCode 结果代码
     * @param resourceBundle 包含消息字符串的
     */
    public ResultCodeMessage(ResultCode resultCode, ResourceBundle resourceBundle, Object[] params) {
        this.resultCode         = resultCode;
        this.resourceBundle     = resourceBundle;
        this.params             = params;

        if (resultCode == null) {
            throw new IllegalArgumentException("ResultCode is null");
        }
    }

    /**
     * 取得message所表达的<code>ResultCode</code>名称。
     *
     * @return <code>ResultCode</code>名称
     */
    public String getName() {
        return resultCode.getName();
    }

    /**
     * 取得message所表达的<code>ResultCode</code>值。
     *
     * @return <code>ResultCode</code>值
     */
    public ResultCode getResultCode() {
        return resultCode;
    }

    /**
     * 取得message字符串。
     *
     * @return message字符串
     */
    public String getMessage() {
        return MessageUtil.getMessage(resourceBundle, resultCode.getName(), params);
    }

    /**
     * 取得message字符串。
     *
     * @return message字符串
     */
    public String toString() {
        return getMessage();
    }
}

