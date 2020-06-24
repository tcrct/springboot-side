package com.springbootside.duang.redis;

import com.springbootside.duang.redis.serializer.FstSerializer;
import com.springbootside.duang.redis.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisFactory.class);

    private static final Map<String, Redis> REDIS_MAP = new ConcurrentHashMap<>();

    private String cacheName;
    private String host;
    private Integer port = null;
    private Integer timeout = null;
    private String password = null;
    private Integer database = null;
    private String clientName = null;

    private ISerializer serializer = null;
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();


    public RedisFactory(String cacheName, String host, int port, int timeout, String password, int database) {
        this.cacheName = cacheName;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
    }
    public RedisFactory(String cacheName, String host, int port, int timeout, String password, int database, String clientName) {
        this(cacheName, host, port, timeout, password, database);
        if (null == clientName || "".equals(clientName))
            throw new IllegalArgumentException("clientName can not be blank.");
        this.clientName = clientName;
    }

    public boolean start() {
        JedisPool jedisPool;
        if (port != null && timeout != null && database != null && clientName != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database, clientName);
        else if (port != null && timeout != null && database != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        else if (port != null && timeout != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        else if (port != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port);
        else
            jedisPool = new JedisPool(jedisPoolConfig, host);

        if (serializer == null)
            serializer = FstSerializer.me;

        addCache(new Redis(cacheName, jedisPool, serializer));
        return true;
    }

    public boolean stop() {
        return removeCache(cacheName);
    }

    public void addCache(Redis redis) {
        REDIS_MAP.put(redis.getRedisName(), redis);
    }

    public static Redis getCache(String cacheName) {
        Redis redis =  REDIS_MAP.get(cacheName);
        if (null == redis) {
            throw new NullPointerException("根据"+cacheName+"取不到Jedis");
        }
        return redis;
    }

    public boolean removeCache(String cacheName) {
        Redis redis = REDIS_MAP.get(cacheName);
        try {
            if (null != redis) {
                redis.close();
            }
        } catch (Exception e) {
            LOGGER.warn("移除[{}]Redis时出错:{}, {}", cacheName, e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 当RedisPlugin 提供的设置属性仍然无法满足需求时，通过此方法获取到
     * JedisPoolConfig 对象，可对 redis 进行更加细致的配置
     * <pre>
     * 例如：
     * redisPlugin.getJedisPoolConfig().setMaxTotal(100);
     * </pre>
     */
    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

}
