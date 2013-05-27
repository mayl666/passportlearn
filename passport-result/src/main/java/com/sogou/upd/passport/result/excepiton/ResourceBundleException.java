package com.sogou.upd.passport.result.excepiton;

import java.io.PrintWriter;
import java.text.MessageFormat;

/**
 * 表示<code>ResourceBundle</code>未找到, 或创建失败的异常.
 * <p/>
 * User: shipengzhi
 * Date: 13-5-24
 * Time: 上午12:54
 * To change this template use
 */
public class ResourceBundleException extends java.util.MissingResourceException implements ChainedThrowable {
    private static final long serialVersionUID = 3258408434930825010L;
    private Throwable cause;

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     *
     * @param messageId  详细信息ID
     * @param params     详细信息参数
     * @param cause      异常的起因
     * @param bundleName bundle名称
     * @param key        resource key
     */
    public ResourceBundleException(String messageId, Object[] params, Throwable cause,
                                   String bundleName, Object key) {
        super(MessageFormat.format(messageId, (params == null) ? new Object[0]
                : params), bundleName,
                String.valueOf(key));
        this.cause = cause;
    }

    /**
     * 取得bundle名.
     *
     * @return bundle名
     */
    public String getBundleName() {
        return super.getClassName();
    }

    /**
     * 取得引起这个异常的起因.
     *
     * @return 异常的起因.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * 打印异常的调用栈, 不包括起因异常的信息.
     *
     * @param writer 打印到输出流
     */
    public void printCurrentStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
    }
}

