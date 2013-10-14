package com.sogou.upd.passport.common.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-10-11
 * Time: 下午7:35
 * To change this template use File | Settings | File Templates.
 */
public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static final Logger redisMissLogger = LoggerFactory.getLogger("redisMissLogger");
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    private ShardedJedisPool shardedJedisPool;

    public Long del(final String key) {
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
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "redies_set")
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
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "redies_setObject")
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

//    /*
//     * 设置缓存内容及有效期，单位为秒
//     */
//    @Profiled(el = true, logger = "rediesTimingLogger", tag = "redies_setEx")
//    public String setWithinSeconds(final String key,final String value,final long timeout) {
//        return new Executor<String>(shardedJedisPool) {
//
//            @Override
//            String execute() throws IOException {
//                return jedis.set(key, );
//            }
//        }.getResult();
//    }

    public Long setNx(final String key, final String value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.setnx(key, value);
            }
        }.getResult();
    }

    public String get(final String key) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.get(key);
            }
        }.getResult();
    }


    public List<String> batchGet(final String[] keys) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                List<String> result = new ArrayList<String>(keys.length);
                List<Response<String>> responses = new ArrayList<Response<String>>(keys.length);
                for (String key : keys) {
                    responses.add(pipeline.get(key));
                }
                pipeline.sync();
                for (Response<String> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();
    }

    public Long hSet(final String key, final String field, final String value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.hset(key, field, value);
            }
        }.getResult();
    }

    public Long hSet(final String key, final String field, final String value, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Long> result = pipeline.hset(key, field, value);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
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
