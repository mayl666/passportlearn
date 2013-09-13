package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
public class SHPlusTokenServiceTest extends BaseTest {

    @Autowired
    private SHPlusTokenService shPlusTokenService;

    @Test
    public void testVerifyShPlusRefreshToken(){
        String passportId = "shipengzhi1986@sogou.com";
        int clientId = 1065;
        String instanceId = "112950932";
        String refreshToken = "43399244b170cef4264a0cac9f53cd495eb4157f2aee5c86d3734528c93136d8";
        shPlusTokenService.verifyShPlusRefreshToken(passportId,clientId,instanceId,refreshToken);
    }

    /**
     * https://account.sogou.com/oauth2/resource/?client_id=30000004&scope=all&instance_id=1213178981&resource_type=full.get
     * &access_token=94875c31900ce878b995095ee911d9e950f59e81cf218cca25d42adcc3c6ade5&h=DF9BB5F023D9D0007F4EC6345416E8FE&r=2170&v=4.2.0.8850
     */
    /**
     * http://open.account.sohu.com/oauth2/resource/?scope=all&resource_type=full.get&client_id=30000004
     * &access_token=94875c31900ce878b995095ee911d9e950f59e81cf218cca25d42adcc3c6ade5&instance_id=1213178981
     */
    @Test
    public void testGetResourceByToken(){

        //http://open.account.sohu.com/oauth2/resource/?
        // scope=all&
        // resource_type=full.get&
        // client_id=30000004&
        // access_token=94875c31900ce878b995095ee911d9e950f59e81cf218cca25d42adcc3c6ade5&
        // instance_id=1213178981
        String instanceId = "1213178981";
        String accesstoken = "60f1cdd4c9e48495ae7e8865aa78b0ad0c2ea508c20c89a438364892481ce786";
        String scope ="all";
        String resource_type ="full.get";

        String json = shPlusTokenService.getResourceByToken(instanceId,accesstoken,scope,resource_type);
        System.out.println("json:"+json);
    }

}
