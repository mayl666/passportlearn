package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.DateUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-13
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String args[]) throws Exception{
//        String str= "\\'\"@#$%^&*";
//        String str= "你好，家盟abc-";
//
//        String encodeStr = Coder.encodeUTF8(str);
//        System.out.println("encodeStr:"+encodeStr);
//
//        String deStr = URLDecoder.decode(encodeStr);
//        System.out.println("decodeStr:"+deStr);
//        String passportId ="￥ﾤﾧ￥ﾤﾧ￥ﾤﾧ31231@focus.cn";
//        String passportId1 = "￥ﾤﾧ￥ﾤﾧ￥ﾤﾧ31231@focus.cn";
//        String tmpPassportId =  new String(passportId.getBytes(), "GBK");
//
////        InputStream in = new InputStream();
////        InputStreamReader in = new InputStreamReader();
//        System.out.println("tmpPassportId:"+tmpPassportId);
//        String passportId2= "%c4%e3%ba%c312345678%40focus.cn";
//        System.out.println("code:"+Coder.decodeUTF8(passportId2));
//        System.out.println("code:"+URLDecoder.decode(passportId2,"GBK"));
//
//        String passportId3 = "���2345678@focus.cn";
//        String passportId4 = Coder.encodeUTF8(passportId3);
//        System.out.println("encode:"+ passportId4);
//        System.out.println("code5:"+URLDecoder.decode(passportId4,"GBK"));
//                (passportId2,"GBK"));


//        String tmpPassportId =  new String(passportId3.getBytes(), "utf-8");
//        String passportId4 = "���2345678@focus.cn";
//        System.out.println("tmpPassportId:"+tmpPassportId);

//        long ct = 1387264053119l;
//        String  userId = "大大大31231@focus.cn";
//        String code = userId + 1120 + "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY" + ct;
//        code = Coder.encryptMD5GBK(code);
//        System.out.println("code:"+code);


        String queryStr = "h=DF9BB5F023D9D0007F4EC6345416E8FE&r=2170&v=4.1.3.8974&appid=1044&userid=%c4%e3%ba%c312345678%40focus.cn&cb=%c4%e3%ba%c312345678%40focus.cn&token=SGmkuumkiavvFqhd5LQ2vAQST0icbq0PqyAEuDauMV6j3lbQibjs3ibEH2BGHaf4X7TibJV&livetime=0&authtype=0&ru=http://profile.ie.sogou.com/&ts=2147483647";
        String tmpstr= queryStr.substring(queryStr.indexOf("userid="),queryStr.length());
        String userid =  tmpstr.substring("userid=".length(),tmpstr.indexOf("&"));
        System.out.println("userid:"+userid);




        Date strartDate = DateUtil.parse("2011-01-01", DateUtil.DATE_FMT_3);
        Date endDate = new Date();
        int dateNum = DateUtil.getDayNum(strartDate, endDate);
        System.out.println("dateNum:"+dateNum);




    }
}
