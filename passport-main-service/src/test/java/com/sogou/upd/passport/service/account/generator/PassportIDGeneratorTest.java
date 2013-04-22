package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午2:34
 * To change this template use File | Settings | File Templates.
 */
public class PassportIDGeneratorTest extends BaseGeneratorTest {

    @Test
    public void testGenerator() {
        boolean phone = PASSPORT_ID_PHONE.equals(PassportIDGenerator.generator(PHONE, AccountTypeEnum.PHONE.getValue()));
        boolean email = PASSPORT_ID_EMAIL.equals(PassportIDGenerator.generator(EMAIL, AccountTypeEnum.EMAIL.getValue()));
        boolean openid = PASSPORT_ID_OPENID.equals(PassportIDGenerator.generator(OPENID, PROVIDER));
        Assert.assertTrue(phone && email && openid);
    }


}
