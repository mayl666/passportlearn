package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.lang.StringUtil;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-16
 * Time: 上午11:57
 * To change this template use File | Settings | File Templates.
 */
public class LogUtil {

    /**
     * 记录数据迁移过程中的异常log
     *
     * @param logger
     * @param message      异常原因
     * @param username     异常用户名
     * @param passportId   异常用户对应的主账号
     * @param resultString 搜狐返回的结果
     */
    public static void buildErrorLog(Logger logger, String message, String username, String passportId, String resultString) {
        StringBuilder log = new StringBuilder();
        Date date = new Date();
        log.append(new SimpleDateFormat("yyy-MM-dd_HH:mm:ss").format(date));                                    //记录时间
        log.append("\t").append(StringUtil.defaultIfEmpty(message, "-"));                                       //记录原因
        log.append("\t").append(StringUtil.defaultIfEmpty(username, "-"));                                      //记录用户名
        log.append("\t").append(StringUtil.defaultIfEmpty(passportId, "-"));                                    //记录主账号
        log.append("\t").append(StringUtil.defaultIfEmpty(resultString, "-"));                                  //记录搜狐返回的结果
        logger.error(log.toString());                                                                           //写log
    }
}
