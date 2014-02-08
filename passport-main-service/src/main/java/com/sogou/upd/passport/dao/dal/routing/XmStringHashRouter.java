package com.sogou.upd.passport.dao.dal.routing;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-2-8
 * Time: 上午1:57
 * To change this template use File | Settings | File Templates.
 */
public class XmStringHashRouter extends XmHashRouter {

    /**
     * @param column
     * @param pattern
     * @param count
     */
    public XmStringHashRouter(String column, String pattern, int count) {
        super(column, pattern, count);
    }

    @Override
    protected long convert(Object columnValue) {
        return stringHash(String.valueOf(columnValue));
    }

    // Copied from MiddleTier.
    private long stringHash(String str) {
        /*
         * djb2 This algorithm was first reported by Dan Bernstein many years
         * ago in comp.lang.c
         */
        long hash = 5381;
        int idx = 0, len = str.length();
        char c;
        while (idx < len) {
            c = str.charAt(idx);
            hash = ((hash << 5) + hash) + c; // hash*33 + c
            idx++;
        }
        if (hash < 0) {
            hash = -hash;
        }
        return hash;
    }
}