package com.sogou.upd.passport.common.mongodb.util;

import com.mongodb.DBCollection;
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

}
