package com.springbootside.duang.redis;

import com.springbootside.duang.redis.core.CacheException;
import com.springbootside.duang.redis.core.CacheKeyModel;
import com.springbootside.duang.redis.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Map;

public class Redis {

    private static final Logger LOG = LoggerFactory.getLogger(Redis.class);

    private String name;
    private JedisPool jedisPool;
    private ISerializer serializer;
    private static Redis REDIS;

    public Redis(String name, JedisPool jedisPool, ISerializer serializer) {
        this.name = name;
        this.jedisPool = jedisPool;
        this.serializer = serializer;
        REDIS = this;
    }

    public static Redis getInstance() {
        return REDIS;
    }

    private Jedis getResource()  {
        try {
            if(null == jedisPool) {
                throw new CacheException("jedisPool is null");
            }
            return jedisPool.getResource();
        } catch (Exception e) {
            throw new CacheException("取jedis资料时出错: " + e.getMessage(), e);
        }
    }

    public void close() {
        jedisPool.close();
    }

    private void close(Jedis jedis) {
        if ( jedis != null) {
            jedis.close();
        }
    }

    /**
     * 调用缓存方法
     * @param action
     * @param <T>
     * @return
     */
    public <T> T call(JedisAction action) {
        T result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = (T) action.execute(jedis);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        finally {
            close(jedis);
        }
        return result;
    }

    /**
     * 序列化key
     * @param key 要缓存的key值
     * @return
     */
    protected byte[] serializerKey(Object key) {
        return serializer.keyToBytes(String.valueOf(key));
    }

    /**
     * 序列化value
     * @param value 要缓存的value值
     * @return
     */
    private byte[] serializeValue(Object value) {
        return serializer.valueToBytes(value);
    }

    /**
     * 反序列化value
     * @param bytes 要反序列化的字节数组
     * @return
     */
    protected Object deSerializeValue(byte[] bytes) {
        return serializer.valueFromBytes(bytes);
    }

    /*************************** Redis里的方法 ************************/

    /**
     * 存放 key value 对到 redis
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     */
    public boolean set(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                String result = jedis.set(serializerKey(model.getKey()), serializeValue(value));
                boolean isOk =  "OK".equalsIgnoreCase(result);
                if(isOk) {
                    expire(model);
                }
                return isOk;
            }
        });
    }

    /**
     * 返回 key 所关联的 value 值
     * 如果 key 不存在那么返回特殊值 nil 。
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final CacheKeyModel model) {
        return call(new JedisAction<T>(){
            @Override
            public T execute(Jedis jedis) {
                return (T)deSerializeValue(jedis.get(serializerKey(model.getKey())));
            }
        });
    }

    /**
     * 根据key设置过期时间
     * @param model  CacheModel对象
     * @return
     *  1 如果成功设置过期时间。
     * 0  如果key不存在或者不能设置过期时间。
     */
    public Long expire(final CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(model.getKeyTTL() > 0) {
                    return jedis.expire(model.getKey(), model.getKeyTTL());
                }
                return 0L;
            }
        });
    }

    /**
     * 根据key删除指定的内容
     * 如果key值不存在，则忽略
     * @param model
     * @return
     */
    public Long del(final CacheKeyModel model){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(model.getKey());
            }
        });
    }

    /**
     * 同时设置一个或多个 key-value 对。
     * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值，如果这不是你所希望的效果，请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
     * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
     * <pre>
     * 例子：
     * Cache cache = RedisKit.use();			// 使用 Redis 的 cache
     * cache.mset("k1", "v1", "k2", "v2");		// 放入多个 key value 键值对
     * List list = cache.mget("k1", "k2");		// 利用多个键值得到上面代码放入的值
     * </pre>
     */
    public Boolean mset(final CacheKeyModel model, final Map<String, String> values) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                int size = values.size();
                byte[][] kv = new byte[size][];
                int i = 0;
                for (Iterator<Map.Entry<String,String>> it = values.entrySet().iterator(); it.hasNext(); ){
                    Map.Entry<String,String> entry = it.next();
                    if (i % 2 == 0) {
                        kv[i] = serializerKey(entry.getKey());
                    } else {
                        kv[i] = serializeValue(entry.getValue());
                    }
                    i++;
                }
                boolean isOk = "OK".equalsIgnoreCase(jedis.mset(kv));
                if(isOk) {
                    expire(model);
                }
                return isOk;
            }
        });
    }
}
