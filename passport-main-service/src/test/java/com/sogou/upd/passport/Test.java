package com.sogou.upd.passport;

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
        String passportId ="大大大31231@focus.cn";
        int expiresIn = 604800;
        String clientSecret = "c1756a351db27d817225e2a4fd7b3f7d";
        String genToken = TokenGenerator.generatorPcToken(passportId,expiresIn,clientSecret);
        String token ="SG_wPJM2wG8ePMeYR8ZCIytNvnCw3wKFq5rKyng3cWbwWfEN1jJE84GWBoaghfM0g9-";
        String dePassportId = TokenDecrypt.decryptPcToken(token,clientSecret);

        System.out.println("genToken:"+genToken);
        System.out.println(dePassportId);
    }
}
