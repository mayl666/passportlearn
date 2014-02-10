package com.sogou.upd.passport.dao.dal.routing;

import com.xiaomi.common.service.dal.routing.Router;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * 字符串类型ID分表hash算法
 * 对应的mysql函数为：conv(left(md5(userid),2),16,10)%32
 * User: shipengzhi
 * Date: 14-2-8
 * Time: 上午1:57
 * To change this template use File | Settings | File Templates.
 */
public class SGStringHashRouter implements Router {

    // 输出日志
    protected static final Logger logger = LoggerFactory.getLogger(SGStringHashRouter.class);
    protected String column, pattern;
    protected int count;

    /**
     * 创建配置记录。
     *
     * @param column  - 配置的列
     * @param pattern - 数据表的名称模板
     * @param count   - 散列表数目
     */
    public SGStringHashRouter(String column, String pattern, int count) {
        this.column = column;
        this.pattern = pattern;
        this.count = count;
    }

    @Override
    public String getColumn() {
        return column;
    }

    /**
     * 设置配置的列。
     *
     * @param column - 配置的列
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * 返回数据表的名称模板。
     *
     * @return 数据表的名称模板
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * 设置数据表的名称模板。
     *
     * @param pattern - 数据表的名称模板
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 返回散列表数目。
     *
     * @return 散列表数目
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置散列表数目。
     *
     * @param count - 散列表数目
     */
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String doRoute(Object columnValue) {

        if (pattern != null && columnValue != null) {
            int intValue = convert(columnValue);
            int value = (intValue % count);
            String name = MessageFormat.format(pattern, formatPatternValue(value, count));
            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug("Routing on [" + column + " = " + columnValue + ", "
                        + columnValue.getClass() + "]: " + name);
            }
            return name;
        }
        return null;
    }

    protected static int convert(Object columnValue) {
        String stringHash = DigestUtils.md5Hex(String.valueOf(columnValue));
        int modInt = Integer.parseInt(stringHash.substring(0, 2), 16);

        return modInt;
    }

    /*
     * 根据分表数的位数，确定表名的位数；比如分32张表，表名为account_03
     */
    private static String formatPatternValue(int value, int count) {
        int countLen = String.valueOf(count).length();
        String pattern = "";
        for (int i = 0; i < countLen; i++) {
            pattern += "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    public static void main(String[] args) {
        String columnValue = "geleisi@sogou.com";
        int intValue = convert(columnValue);
        int value = (intValue % 32);
        System.out.println("value=" + formatPatternValue(value, 32));
    }

}