package com.sogou.upd.passport.web.account.action;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-21
 * Time: 下午1:11
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ProblemActionTest {
    public static void main(String args[]){
        //HTML clean
        String unsafe = "<table><tr><td>1</td></tr></table>" +
                "<img src='' alt='' />" +  "我，搜狗同学 你好！！！"+
                "<p><a href='http://example.com/' onclick='stealCookies()'>Link</a>" +
                "<object></object>" +
                "<script>alert(1);</script>" +
                "</p>";
//        String safe = Jsoup.clean(unsafe, Whitelist.relaxed());
//        String safe = Jsoup.clean(unsafe, Whitelist.none());
//        System.out.println("safe: " + safe);
    }
}
