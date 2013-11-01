package com.sogou.upd.passport.web.internal.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisShardInfo;

/**
 * 依赖模块开关Controller
 * User: shipengzhi
 * Date: 13-10-31
 * Time: 下午10:14
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ComponentSwitcherController {

    @Autowired
    private JedisConnectionFactory tokenConnectionFactory; //PC端token存储缓存
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;  //web接口临时信息存储缓存

    @RequestMapping(value = "/internal/debug/tokenRedisSwitch", method = RequestMethod.GET)
    @ResponseBody
    public String tokenRedisSwitch(String host, int port) throws Exception {
        JedisShardInfo shardInfo = new JedisShardInfo(host, port);
        tokenConnectionFactory.setHostName(host);
        tokenConnectionFactory.setPort(port);
        tokenConnectionFactory.setShardInfo(shardInfo);
        tokenConnectionFactory.afterPropertiesSet();
        return "OK";
    }

    @RequestMapping(value = "/internal/debug/breakRedisSwitch", method = RequestMethod.GET)
    @ResponseBody
    public String breakRedisSwitch(String host, int port) throws Exception {
        JedisShardInfo shardInfo = new JedisShardInfo(host, port);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setShardInfo(shardInfo);
        jedisConnectionFactory.afterPropertiesSet();
        return "OK";
    }
}
