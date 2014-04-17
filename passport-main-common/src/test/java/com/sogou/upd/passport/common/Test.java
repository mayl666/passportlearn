package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.Result;
import com.sogou.upd.passport.common.utils.XMLUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-13
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String args[]) throws Exception{
        String xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
                        "<result>\n" +
                        "<uid>c25971550</uid>\n" +
                        "<status>0</status>\n" +
                        "<userid>lanzewei@chinaren.com</userid>\n" +
                        "<uuid>427d6c33f71a484c</uuid>\n" +
                        "<uniqname>：^N：鳕花紛飛：^N：</uniqname>\n" +
                        "</result>";
        Result result = XMLUtil.xmlToBean(xml, Result.class);
        System.out.println(result.getStatus());
    }
}
