package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-9-19
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class AccountRoamManagerTest extends BaseTest {
    @Autowired
    private AccountRoamManager accountRoamManager;

    @Test
    public void testGetUserIdByBrowerRoam() throws IOException {
        String token = "lqz5+RmbnzcoX/Qz+OUJAelP5FG0Ko5mS+6VCjn2t1aXDctPDFzFWQk4XDpBB6JX2glhuAPbFMGt6QeB1nIhP7+a3HB7neUlz0BVg8M7X1StFLWDrzzrjVwFnICJu1h1TTX/gr9ss/a7zIgOzOgNzA9rRs37v+O/zmWe9xdQMKRGQqUTWfw7dXjxliqk0/D5DFOk/yT2ntM0i82hmQDjPNpmbSTekEUezKeUupEkqUxTyJQG/cgXZVkv5keg2blW00TJn3MJb6mLkO3JLihpMgsTnZ+WKT+MG7AKgCM1+hDQMXmeV53eE/WUWo5mkN3r8Dfba3Ikj+QuDrZxeU6T3A==";
        String userId = accountRoamManager.getUserIdByBrowerRoamToken(token);
        System.out.println("passportId:" + userId);

        String cookie = "M2007FlQvuwXOWZibjQawWCTZYyAnL+y7as/BXhL2B0xsYeoTApInFUKooMnmW4FwyEuVFC/anoXsbkyoZzREGpLdimwRhcHsd27anubmkBj8qsskYh+FUj8o3YnaYhM7Ze3tIAYAEdvkovn1///Fw2aN1o4vN7Mvfv+PuxyUYgv/ANhTmp4sISKjX7tj/hYPIikk4yT7AWsJNQeFRLWGjjlWU8Oa2KGtL+P2+33iV1q80exyrKhaIjB9seN8ikDYd0APbG1gQoZcYsa42toUXMHB41PrcTa4ASEl26PPvlPdOBR/TfaOV+eR0ng0/fxmjmPSjdnL4kXDtD/DLOiul7//QBzegXueYFoPHvdhoQdnPeghsC5CWB/Qhkw+92Qb2gqqjcGpw3jxrJ0fCoe+huIxil+MdXDLwe/HZz/Q63Xd+YVcFJ4FM3nBD82BbyYZl47KzzUqp9jE38hgsD3tz/X++9e8u0mzZg3RIpBkxI2Lrjdm4T5dmKlsPRhU54XdKBiRIchS+zdimz+JT448qpvhod1qxTDXvDQXFK18KNTt9x2+5BCuZz+ctKUiIcDAlK3d5BzA2OP5fzjHRKYKhuMGD4pOwsZgfUgkLa0tXyOQWrKnkw7DCW+2naWELS8q0g1g1FkJfm93DqD3A8njxpL0t5zT0BI//65n8lL73Zo6yS4OvoV/oR7+46Tml5rxsCuSomL14izURzhOhRiN/SVGR/CD9o8hHkRKVlSlFl5UZeIjKzQEP4i2G04W4KtEEmB7umX1Te9rh4N9S7BnCrFVrmkPHAYsEmyB23cQrIIpKJbz6c5Muh6MODDRpBXnd5bJbWLZMGNSSrEucKoaSOad7ZIsJK8NDTBVm8YiUJSz+b6qX0AV4PdkOan+/QLA7DOWYrt7jQBKfGAbV3k4d8yZ3hwPZrhGbQiaQjY1KW5jwP7fTbgQmcLEQVgMKgDkknNYNmc430/URPGR7MELgDF+ATZcnY3pOCnPjSGqXUZj48Ba/qZdIHWNFXrv0RQ";
        userId = accountRoamManager.getUserIdByBrowerRoamCookie(cookie);
        System.out.println("passportId:"+userId);
    }
}
