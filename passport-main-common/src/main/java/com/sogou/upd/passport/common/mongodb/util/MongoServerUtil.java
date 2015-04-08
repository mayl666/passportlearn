package com.sogou.upd.passport.common.mongodb.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.perf4j.aop.Profiled;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 简单封装一下，避免MongoTemplate直接暴露出去，由于目前MongoTemplate只支持到2.4版的mongodb，我们使用的时2.6版本的mongodb，之后可能更换
 * User: ligang201716@sogou-inc.com
 * Date: 14-7-7
 * Time: 下午3:20
 */
public class MongoServerUtil {

    private MongoTemplate mongoTemplate = null;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public DBCollection getCollection(String collection) {
        return mongoTemplate.getCollection(collection);
    }

    @Profiled(el = true, logger = "mongodbTimingLogger", tag = "mongodb_findOne", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public DBObject findOne(String DBCollection, BasicDBObject basicDBObject) {
        DBCollection dbCollection = this.getCollection(DBCollection);
        DBObject dbObject = dbCollection.findOne(basicDBObject);
        return dbObject;
    }

}
