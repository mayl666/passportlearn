package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis工具类
 * User: mayan
 * Date: 14-2-19
 * Time: 上午11:31
 */
public class DBShardRedisUtils {
    private static Logger logger = LoggerFactory.getLogger(DBShardRedisUtils.class);
    private static final Logger redisMissLogger = LoggerFactory.getLogger("redisMissLogger");
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    private ShardedJedisPool shardedJedisPool;

    public Long delete(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.del(key);
            }
        }.getResult();
    }

    public Long expire(final String key, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.expire(key, expire);
            }
        }.getResult();
    }

    /*
    * 设置缓存内容
    */
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_set")
    public String set(final String key, final String value) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.set(key, value);
            }
        }.getResult();
    }

    /*
* 设置缓存内容
*/
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_setObject")
    public String set(final String key, final Object obj) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() throws IOException {
                return jedis.set(key, jsonMapper.writeValueAsString(obj));
            }
        }.getResult();
    }


    public String set(final String key, final String value, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.setex(key, expire, value);
            }
        }.getResult();
    }

    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_setWithinSeconds", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public String setWithinSeconds(final String key, final Object obj, final long timeout) {
        return new Executor<String>(shardedJedisPool) {
            ShardedJedisPipeline pipeline = null;

            @Override
            String execute() throws IOException {
                pipeline = jedis.pipelined();
                Response result = pipeline.set(key, jsonMapper.writeValueAsString(obj));
                pipeline.expire(key, (int) timeout);
                pipeline.sync();
                return (String) result.get();
            }
        }.getResult();
    }

    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_hPutAllObject", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public <T> String hPutAllObject(final String cacheKey, final Map<String, T> mapData) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() throws IOException {

                Map<String, String> objectMap = Maps.newHashMap();
                Set<String> keySet = mapData.keySet();
                for (String key : keySet) {
                    T obj = mapData.get(key);
                    if (obj != null) {
                        objectMap.put(key, jsonMapper.writeValueAsString(obj));
                    }
                }

                return jedis.hmset(cacheKey, objectMap);
            }
        }.getResult();
    }

    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_hPut", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public Long hPut(final String cacheKey, final String key, final Object obj) throws Exception {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() throws IOException {
                return jedis.hset(cacheKey, key, jsonMapper.writeValueAsString(obj));
            }
        }.getResult();
    }

    /*
    * 设置缓存内容
    * 冲突不覆盖
    */
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_setNx", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public Long setNx(final String key, final String value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.setnx(key, value);
            }
        }.getResult();
    }

    /*
   * 根据key取缓存内容
   */
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_get", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public String get(final String key) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.get(key);
            }
        }.getResult();
    }

    public String hGet(final String key, final String field) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.hget(key, field);
            }
        }.getResult();
    }

    public String hGet(final String key, final String field, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<String> result = pipeline.hget(key, field);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    public String hmSet(final String key, final Map<String, String> hash) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.hmset(key, hash);
            }
        }.getResult();
    }

    public String hashMultipleSet(final String key, final Map<String, String> hash, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<String> result = pipeline.hmset(key, hash);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    public List<String> hashMultipleGet(final String key, final String... fields) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                return jedis.hmget(key, fields);
            }
        }.getResult();
    }

    public List<String> hashMultipleGet(final String key, final int expire, final String... fields) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<List<String>> result = pipeline.hmget(key, fields);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    public Map<String, String> hGetAll(final String key) {
        return new Executor<Map<String, String>>(shardedJedisPool) {

            @Override
            Map<String, String> execute() {
                return jedis.hgetAll(key);
            }
        }.getResult();
    }

    public Map<String, String> hGetAll(final String key, final int expire) {
        return new Executor<Map<String, String>>(shardedJedisPool) {

            @Override
            Map<String, String> execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Map<String, String>> result = pipeline.hgetAll(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * 根据key取对象
     */
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "dbShardRedis_getObject", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public <T> T getObject(final String cacheKey, final Class returnClass) {
        return new Executor<T>(shardedJedisPool) {
            @Override
            T execute() throws IOException {
                String cacheStr = get(cacheKey);
                T object = null;
                if (!Strings.isNullOrEmpty(cacheStr)) {
                    object = (T) jsonMapper.readValue(cacheStr, returnClass);
                }
                return object;
            }
        }.getResult();
    }

    abstract class Executor<T> {

        ShardedJedis jedis;
        ShardedJedisPool shardedJedisPool;

        public Executor(ShardedJedisPool shardedJedisPool) {
            this.shardedJedisPool = shardedJedisPool;
            jedis = this.shardedJedisPool.getResource();
        }

        abstract T execute() throws IOException;

        public T getResult() {
            T result = null;
            try {
                result = execute();
            } catch (Throwable e) {
                throw new RuntimeException("Redis execute exception", e);
            } finally {
                if (jedis != null) {
                    shardedJedisPool.returnResource(jedis);
                }
            }
            return result;
        }
    }
}
