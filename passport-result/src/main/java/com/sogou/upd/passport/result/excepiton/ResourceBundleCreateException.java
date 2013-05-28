package com.sogou.upd.passport.result.excepiton;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-24
 * Time: 上午1:21
 * To change this template use File | Settings | File Templates.
 */

import java.text.MessageFormat;

/**
 * 表示创建<code>ResourceBundle</code>失败的异常.
 *
 * @author Michael Zhou
 * @version $Id: ResourceBundleCreateException.java 1291 2005-03-04 03:23:30Z baobao $
 */
public class ResourceBundleCreateException extends ChainedException {
    private static final long serialVersionUID = 3258132457613177654L;

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     *
     * @param messageId 详细信息ID
     * @param params 详细信息参数
     * @param cause 异常的起因
     */
    public ResourceBundleCreateException(String messageId, Object[] params, Throwable cause) {
        super(MessageFormat.format(messageId, (params == null) ? new Object[0]
                : params), cause);
    }
}
