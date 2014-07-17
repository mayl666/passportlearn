package com.sogou.upd.passport.common;

import org.junit.Ignore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-25
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class InterceptRootPathTest {

    private static String getRootPath(String url) {
        try {
            Pattern p = Pattern.compile("(?<=(http|https)://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(url);
            matcher.find();
            String rootPath = matcher.group();
            return rootPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static void main(String[] args) {
        String url1 = "http://s.account.sogou.com?a=123";
        String url2 = "http://anotherbug.blog.sogou.com/entry/4545/0/";
        String url3 = "http://s.sogou.com?a=123";
        String url4 = "http://sogou.com";
        String url5 = "https://sogou.com";
        System.out.println("url1:" + getRootPath(url1));
        System.out.println("url2:" + getRootPath(url2));
        System.out.println("url3:" + getRootPath(url3));
        System.out.println("url4:" + getRootPath(url4));
        System.out.println("url5:" + getRootPath(url5));
    }
}
