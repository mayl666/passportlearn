package com.sogou.upd.passport.common.mongodb.util;

import com.mongodb.*;
import org.apache.commons.lang.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来根据创建mongoclient
 * User: ligang201716@sogou-inc.com
 * Date: 14-7-7
 * Time: 下午4:14
 */
public class MongoFactory {

    public static MongoClient createMongo(String addrs) {
        try {
            if (StringUtils.isBlank(addrs)) {
                return new MongoClient();
            }
            String[] addrArry = addrs.split(",");
            List<ServerAddress> serverAddressList = new ArrayList<>(addrArry.length);
            for (String addr : addrArry) {
                String[] ipPort = addr.split(":");
                ServerAddress serverAddress = null;

                serverAddress = new ServerAddress(ipPort[0], Integer.valueOf(ipPort[1]));

                serverAddressList.add(serverAddress);
            }

            MongoClientOptions mongoClientOptions = MongoClientOptions.builder().connectionsPerHost(128)
                    .minConnectionsPerHost(64)
                    .maxConnectionIdleTime(1800000)
                    .maxWaitTime(200)
                    .socketKeepAlive(true)
                    .build();
            MongoClient mongoClient=new MongoClient(serverAddressList, mongoClientOptions);
            mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
            mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
            return mongoClient;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
