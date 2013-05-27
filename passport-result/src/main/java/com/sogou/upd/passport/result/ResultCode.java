package com.sogou.upd.passport.result;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.i18n.LocaleUtil;
import com.sogou.upd.passport.result.collections.IntegerEnum;
import com.sogou.upd.passport.result.resourcebundle.ResourceBundle;
import com.sogou.upd.passport.result.resourcebundle.ResourceBundleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Command处理结果的代码
 *
 * @author shipengzhi
 * @version 2013-05-23
 */
public class ResultCode extends IntegerEnum {
    private static final long serialVersionUID = 3257848762037777207L;

    /**
     * 表示成功执行AO。
     */
    public static final ResultCode SUCCESS = (ResultCode) create();

    /**
     * 表示AO抛出未预料到异常，或者<code>isSuccess()</code>为<code>false</code>却未指明具体的<code>ResultCode</code>。
     */
    public static final ResultCode GENERIC_FAILURE = (ResultCode) create();

    /**
     * 如果未指定command，或command的名称为空。
     */
    public static final ResultCode MISSING_COMMAND = (ResultCode) create();

    /**
     * 表示command的参数不正确。
     */
    public static final ResultCode ILLEGAL_COMMAND_PARAMETERS = (ResultCode) create();

    /**
     * 表示取AO对象时失败。
     */
    public static final ResultCode GET_APPLICATION_OBJECT_FAILURE = (ResultCode) create();

    /**
     * 发送JMS异步消息时失败。
     */
    public static final ResultCode SEND_ASYNCHRONOUS_MESSAGE_FAILURE = (ResultCode) create();

    /**
     * 处理action event时失败。
     */
    public static final ResultCode PROCESS_ACTION_EVENT_FAILURE = (ResultCode) create();

    /**
     * Logger日志。
     */
    private transient Logger log;

    /**
     * 存放result code描述信息的resouce bundle（locale => bundle）。
     */
    private transient Map resourceBundles;

    /**
     * 创建result code的描述信息。
     *
     * @return <code>ResultCodeMessage</code>对象
     */
    public ResultCodeMessage getMessage() {
        return new ResultCodeMessage(this, getResourceBundle());
    }

    /**
     * 创建result code的描述信息。
     *
     * @return <code>ResultCodeMessage</code>对象
     */
    public ResultCodeMap getMap() {
        return new ResultCodeMap(this, getResourceBundle());
    }

    /**
     * 取得log日志。
     */
    protected final Logger getLogger() {
        if (log == null) {
            log = LoggerFactory.getLogger(getClass());
        }
        return log;
    }

    /**
     * 取得存放result code描述信息的resouce bundle。
     */
    protected final synchronized ResourceBundle getResourceBundle() {
        if (resourceBundles == null) {
            resourceBundles = Maps.newHashMap();
        }

        Locale contextLocale = LocaleUtil.getContext().getLocale();
        ResourceBundle resourceBundle = (ResourceBundle) resourceBundles.get(contextLocale);

        if (resourceBundle == null) {
            Class resultCodeClass = getClass();

            do {
                String resourceBundleName = resultCodeClass.getName();

                getLogger().debug("Trying to load resource bundle: " + resourceBundleName);

                try {
                    resourceBundle = ResourceBundleFactory.getBundle(resourceBundleName);
                } catch (MissingResourceException e) {
                    getLogger().debug("Resource bundle not found: " + resourceBundleName);
                    resultCodeClass = resultCodeClass.getSuperclass();
                }
            } while ((resourceBundle == null) && (resultCodeClass != null));

            resourceBundles.put(contextLocale, resourceBundle);
        }

        return resourceBundle;
    }

}

