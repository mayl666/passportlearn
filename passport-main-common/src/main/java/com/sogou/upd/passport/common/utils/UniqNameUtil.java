package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;

/**
 * 用户昵称工具类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-5
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public class UniqNameUtil {

    /**
     * 检查用户昵称格式是否正确
     *
     * @param value 用户昵称
     * @return true:格式合法；false：格式不合法
     */
    public boolean checkUniqNameIsCorrect(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        return isCheckLength(value) && isCheckSensitive(value);
    }

    /**
     * 检查昵称长度是否符合规则，长度在2——12字符之间
     *
     * @param value
     * @return
     */
    public boolean isCheckLength(String value) {
        boolean flag = value.length() >= 2 && value.length() <= 12;
        if (!flag) {
            return false;
        }
        return true;
    }

    /**
     * 检查昵称中是否符合组成规则,且昵称不能含有搜狐，搜狐微博，sohu，souhu，搜狗，sogou ,sougou字样
     *
     * @param value
     * @return
     */
    public boolean isCheckSensitive(String value) {
        //不能含有如下词，返回false则不合法，否则合法
        String regx = "^(?!.*搜狐)(?!.*搜狐微博)(?!.*sohu)(?!.*souhu)(?!.*搜狗)(?!.*sogou)(?!.*sougou)[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        boolean flag = value.matches(regx);
        if (!flag) {
            return false;
        }
        return true;
    }
}
