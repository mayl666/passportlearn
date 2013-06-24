package com.sogou.upd.passport.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * sogou kv系统
 * User: mayan
 * Date: 13-6-24 Time: 下午6:34
 */
public class KvUtils {

  private static Logger logger = LoggerFactory.getLogger(KvUtils.class);

  private static RedisTemplate kvTemplate;

}
