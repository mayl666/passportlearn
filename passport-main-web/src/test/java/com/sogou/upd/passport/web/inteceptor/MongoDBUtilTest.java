package com.sogou.upd.passport.web.inteceptor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sogou.upd.passport.common.MongodbConstant;
import com.sogou.upd.passport.common.mongodb.util.MongoServerUtil;
import com.sogou.upd.passport.model.rishcontrol.RiskIpData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-23
 * Time: 下午6:13
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring-config-test.xml")
public class MongoDBUtilTest {

    @Autowired
    public MongoServerUtil mongoServerUtil;

    @Test
    public void test() {
        MongoOperations mongoOperations = null;
        DBCollection dbCollection = mongoServerUtil.getCollection(MongodbConstant.RISK_CONTROL_COLLECTION_TEST);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("ip", "10.129.192.147");
        basicDBObject.put("city", "zhejiang");
        basicDBObject.put("regional", "0");
        basicDBObject.put("rate", "1");
        basicDBObject.put("level", "0");
        basicDBObject.put("input_times", "2015-03-24 13:50:10");
        basicDBObject.put("deny_startTime", "2015-03-26 13:50:10");
        basicDBObject.put("deny_endTime", "2015-03-26 13:55:10");
        basicDBObject.put("abnormal_indicators", "pv_ip");
        basicDBObject.put("count_indicators", "1");
        BasicDBObject basicDBObject1 = new BasicDBObject();
        basicDBObject1.put("ip", "10.129.192.147");
        dbCollection.update(basicDBObject1, basicDBObject);

        System.out.println("++++++++++++++" + dbCollection.insert(basicDBObject).getN());

//        DBObject dbObject = dbCollection.findOne(basicDBObject);
//        System.out.print("=========================" + dbObject.toString());
//        DBCursor dbCursor = dbCollection.find(basicDBObject);
//        while (dbCursor.hasNext()) {
//            DBObject dbObject = dbCursor.next();
//            System.out.println("+++++++++++++++++++++" + dbObject.get("ip") + "+++++++++++++++++++++");
//        }
//        System.out.print("======================" + dbCursor.count() + "==================================");
    }
}
