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

    public static final ResultCode SYSTEM_UNKNOWN_EXCEPTION = (ResultCode) create();

    public static final ResultCode SUCCESS = (ResultCode) create();

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

