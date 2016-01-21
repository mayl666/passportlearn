package com.sogou.upd.passport.mongo;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.MongodbConstant;
import com.sogou.upd.passport.common.mongodb.util.MongoServerUtil;
import com.sogou.upd.passport.common.validation.constraints.RiskControlConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-5-19
 * Time: 下午4:03
 */
public class MongodbTest extends BaseTest {


    @Autowired
    public MongoServerUtil mongoServerUtil;


    @Test
    public void testFindOne() {

        String ip = "127.0.0.1";

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(RiskControlConstant.IP, ip);
        DBObject resultObject = mongoServerUtil.findOne(MongodbConstant.RISK_CONTROL_COLLECTION, basicDBObject);
        if (null != resultObject) {
            String regional = String.valueOf(resultObject.get(RiskControlConstant.REGIONAL));
            String endTimeStr = String.valueOf(resultObject.get(RiskControlConstant.DENY_END_TIME));
            if (!Strings.isNullOrEmpty(endTimeStr) && !Strings.isNullOrEmpty(regional)) {
                System.out.println("denyEndTime :" + endTimeStr);
            }
        }

    }
}
