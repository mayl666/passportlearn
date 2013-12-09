package com.sogou.upd.passport;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-2
 * Time: 下午9:33
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String args[]) throws Exception{
//        String passportId ="大大大31231@focus.cn";
//        String passportId ="tinkame731@sohu.com";

        String passportId ="565244600@renren.sohu.com";
//        String passportId ="31680D6A6A65D32BF1E929677E78DE29@qq.sohu.com";
//        String passportId ="chun_xue_1985@sohu.com";
//        String passportId ="jjaabbmm@17173.com";
//        String passportId ="2FE256A04517948849CA6BA96CBD6A70@qq.sohu.com";
//        String passportId ="2FE256A04517948849CA6BA96CBD6A70@qq.sohu.com";
//        int expiresIn = 604800;
////        String clientSecret = "c1756a351db27d817225e2a4fd7b3f7d";
////        String genToken = TokenGenerator.generatorPcToken(passportId,expiresIn,clientSecret);
////        String token ="SG_wPJM2wG8ePMeYR8ZCIytNvnCw3wKFq5rKyng3cWbwWfEN1jJE84GWBoaghfM0g9-";
////        String dePassportId = TokenDecrypt.decryptPcToken(token,clientSecret);
//
//        String passportStr ="￥ﾤﾧ￥ﾤﾧ￥ﾤﾧ31231@focus.cn";
//        String userid = new String(passportStr.getBytes("ISO-8859-1"), "UTF-8");
//
//
////        System.out.println("genToken:"+new String(passportStr.getBytes("utf-8"),"gbk"));
//        System.out.println("userid:"+userid);


        System.out.println("result:"+AccountDomainEnum.isPassportId(passportId));
    }
}
