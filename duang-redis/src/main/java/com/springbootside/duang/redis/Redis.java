package com.springbootside.duang.redis;

import com.springbootside.duang.redis.core.CacheException;
import com.springbootside.duang.redis.core.CacheKeyModel;
import com.springbootside.duang.redis.serializer.ISerializer;
import com.springbootside.duang.redis.serializer.JdkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.util.SafeEncoder;

import java.util.*;

/**
 *  Redis使用方法封装类
 *
 * @author Laotang
 * @since 1.0
 */
public class Redis {

    private static final Logger LOG = LoggerFactory.getLogger(Redis.class);

    protected final ThreadLocal<Jedis> jedisThreadLocal = new ThreadLocal<>();

    private String name;
    private JedisPool jedisPool;
    private ISerializer serializer;
    private boolean isJedisThreadLocal;

    /**
     * 构造方法
     * @param name
     * @param jedisPool
     * @param serializer
     */
    public Redis(String name, JedisPool jedisPool, ISerializer serializer) {
        this.name = name;
        this.jedisPool = jedisPool;
        this.serializer = serializer;
    }

    public String getRedisName() {
        return name;
    }

    private Jedis getResource()  {
        try {
            Jedis jedis = jedisPool.getResource();
            if (null == jedis) {
                throw new CacheException("jedis is null");
            }
            return jedis;
        } catch (Exception e) {
            throw new CacheException("取jedis资料时出错: " + e.getMessage(), e);
        }
    }
    /*
    private Jedis getResource()  {
        try {
            Jedis jedis = jedisThreadLocal.get();
            if (null != jedis) {
//                isJedisThreadLocal = true;
                return jedis;
            }
            if(null == jedisPool) {
                throw new CacheException("jedisPool is null");
            }
            jedis =  jedisPool.getResource();
            if (null == jedis) {
                throw new CacheException("jedis is null");
            }
            isJedisThreadLocal = false;
            jedisThreadLocal.set(jedis);
            return jedis;
        } catch (Exception e) {
            throw new CacheException("取jedis资料时出错: " + e.getMessage(), e);
        }
    }
     */

    public void close() {
        jedisPool.close();
    }

    private void close(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
        /*
        if (jedisThreadLocal.get() == null && jedis != null) {
            jedis.close();
            jedisThreadLocal.remove();
        }
         */
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
    private byte[] serializerKey(Object key) {
        return serializer.keyToBytes(String.valueOf(key));
    }

    private byte[][] serializerKeyArray(Object... keys) {
        byte[][] result = new byte[keys.length][];
        for (int i=0; i<result.length; i++)
            result[i] = serializerKey(keys[i]);
        return result;
    }

    /**
     * 序列化value
     * @param value 要缓存的value值
     * @return
     */
    private byte[] serializerValue(Object value) {
//        return SafeEncoder.encode((String)value);
        return serializer.valueToBytes(value);
    }

    private byte[][] serializerValueArray(Object... valuesArray) {
        byte[][] data = new byte[valuesArray.length][];
        for (int i=0; i<data.length; i++)
            data[i] = serializerValue(valuesArray[i]);
        return data;
    }



    private byte[] serializeValuesTR(Object value) {
        return serializer.valueToBytes(value);
    }

    protected String deSerializeKey(byte[] bytes) {
        return serializer.keyFromBytes(bytes);
    }
    /**
     * 反序列化value
     * @param bytes 要反序列化的字节数组
     * @return
     */
    protected Object deSerializeValue(byte[] bytes) {
        return serializer.valueFromBytes(bytes);
    }
    protected <T> List<T> valueListFromBytesList(List<byte[]> data) {
        List<T> result = new ArrayList<>(data.size());
        for (byte[] d : data) {
            result.add((T)deSerializeValue(d));
        }
        return result;
    }

    protected Set<String> valueListFromBytesSet(Set<byte[]> data) {
        LinkedHashSet<String> result = new LinkedHashSet<>(data.size());
        for (byte[] d : data) {
            result.add(String.valueOf(deSerializeValue(d)));
        }
        return result;
    }



    /*************************** Redis里的方法 ************************/

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
     * 存放 key value 对到 redis
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     */
    public boolean set(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                String result = jedis.set(serializerKey(model.getKey()), serializerValue(value));
                boolean isOk =  "OK".equalsIgnoreCase(result);
                if(isOk) {
                    expire(model);
                }
                return isOk;
            }
        });
    }

    /**
     * 存放 key value 对到 redis，并将 key 的生存时间设为 seconds (以秒为单位)。
     * 如果 key 已经存在， SETEX 命令将覆写旧值。
     */
    public String setex(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                String result = jedis.setex(serializerKey(model.getKey()), model.getKeyTTL(), serializerValue(value));
                boolean isOk =  "OK".equalsIgnoreCase(result);
                if(isOk) {
                    expire(model);
                }
                return isOk;
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
                    return jedis.expire(serializerKey(model.getKey()), model.getKeyTTL());
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
     * 查找所有符合给定模式 pattern 的 key 。
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * 特殊符号用 \ 隔开
     */
    public Set<String> keys(String pattern) {
//        LOG.warn("生产环境下禁用");
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.keys(pattern);
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
                        kv[i] = serializerValue(entry.getValue());
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

    /**
     * 返回所有(一个或多个)给定 key 的值。
     * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
     *
     * @param keys 自定义的key 值
     */
    @SuppressWarnings("rawtypes")
    public List<String> mget(Object... keys) {
        return call(new JedisAction<List<String>>(){
            @Override
            public List<String> execute(Jedis jedis) {
                byte[][] keysArray = serializerKeyArray(keys);
                List<byte[]> byteList = jedis.mget(keysArray);
                if (null == byteList) {
                    return null;
                }
                List<String> list = new ArrayList<>(byteList.size());
                for (byte[] bytes : byteList) {
                    list.add(String.valueOf(deSerializeValue(bytes)));
                }
                return list;
            }
        });
    }

    /**
     * 将 key 中储存的数字值减一。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 关于递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
     */
    public Long decr(Object key) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.decr(serializerKey(key));
            }
        });
    }

    /**
     * 将 key 所储存的值减去减量 decrement 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 关于更多递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
     */
    public Long decrBy(Object key, long longValue) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.decrBy(serializerKey(key), longValue);
            }
        });
    }

    /**
     * 将 key 中储存的数字值增一。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     */
    public Long incr(Object key) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.incr(serializerKey(key));
            }
        });
    }

    /**
     * 将 key 所储存的值加上增量 increment 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 关于递增(increment) / 递减(decrement)操作的更多信息，参见 INCR 命令。
     */
    public Long incrBy(Object key, long longValue) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.incrBy(serializerKey(key), longValue);
            }
        });
    }

    /**
     * 检查给定 key 是否存在。
     */
    public Boolean exists(Object key) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(serializerKey(key));
            }
        });
    }

    /**
     * 将 key 改名为 newkey 。
     * 当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
     * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     */
    public String rename(Object oldkey, Object newkey) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.rename(serializerKey(oldkey),serializerKey(newkey));
            }
        });
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
     * 当 key 存在但不是字符串类型时，返回一个错误。
     */
    @SuppressWarnings("unchecked")
    public <T> T getSet(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<T>(){
            @Override
            public T execute(Jedis jedis) {
                try {
                    Object object = deSerializeValue(jedis.getSet(serializerKey(model.getKey()), serializerValue(value)));
                    if (null != object) {
                        expire(model);
                    }
                    return (T)object;
                } catch (Exception e) {
                    LOG.warn("redis执行getSet操作时失败: {}, {}", e.getMessage(), e);
                    return null;
                }
            }
        });
    }

    /**
     * 移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
     */
    public Long persist(final CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.persist(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 返回 key 所储存的值的类型。
     */
    public String type(final CacheKeyModel model) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.type(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     */
    public Long ttl(final CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.ttl(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 对象被引用的数量
     */
    public Long objectRefcount(final CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.objectRefcount(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 对象没有被访问的空闲时间
     */
    public Long objectIdletime(final CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.objectIdletime(serializerKey(model.getKey()));
            }
        });
    }


    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，
     * 对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
     * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     */
    public Long lpush(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.lpush(serializerKey(model.getKey()), serializerValue(value));
            }
        });
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     */
    public Long hset(final CacheKeyModel model, Object field, Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Long count =  jedis.hset(serializerKey(model.getKey()), serializerKey(field), serializerValue(value));
                if (count > 0) {
                    expire(model);
                }
                return count;
            }
        });
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
     * 此命令会覆盖哈希表中已存在的域。
     * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     */
    public Boolean hmset(final CacheKeyModel options, final Map<String, Object> values) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                boolean isOk = false;
                if(null != values){
                    Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(values.size());
                    for (Iterator<Map.Entry<String,Object>> it = values.entrySet().iterator(); it.hasNext(); ){
                        Map.Entry<String,Object> entry = it.next();
                        map.put(serializerKey(entry.getKey()), serializerValue(entry.getValue()));
                    }
                    isOk = "OK".equalsIgnoreCase(jedis.hmset(serializerKey(options.getKey()), map));
                    if(isOk) {
                        expire(options);
                    }
                }
                return isOk;
            }
        });
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param model
     * @param field
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T hget(final CacheKeyModel model, final String field) {
        return call(new JedisAction<T>() {
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.hget(serializerKey(model.getKey()),  serializerValue(field));
                return (T)deSerializeValue(bytes);
            }
        });
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     * @param model                        CacheKeyModel
     * @param fields	hash中的field
     * @return
     */
    public <T> List<T> hmget(final CacheKeyModel model, final String... fields) {
        return call(new JedisAction<List<T>>() {
            @Override
            public List<T>execute(Jedis jedis) {
                List<byte[]> data = jedis.hmget(serializerKey(model.getKey()), serializerKeyArray(fields));
                if (null != data){
                    expire(model);
                }
                return valueListFromBytesList(data);
            }
        });
    }

    /**
     * 返回名称为key的hash中fields对应的value
     * @param model                        CacheModelOptions对象
     * @param fields	hash中的field
     * @return
     */
    public Map<String,Object> hmgetToMap(final CacheKeyModel model, final String... fields) {
        return call(new JedisAction<Map<String,Object>>() {
            @Override
            public Map<String, Object> execute(Jedis jedis) {
                List<byte[]> byteList = jedis.hmget(serializerKey(model.getKey()), serializerKeyArray(fields));
                int size  = byteList.size();
                Map<String,Object> map = new HashMap<>(size);
                for (int i = 0; i < size; i ++) {
                    if(null != byteList.get(i)) {
                        map.put(fields[i], deSerializeValue(byteList.get(i)));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     */
    public Long hdel(final CacheKeyModel model, final String... fields) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hdel(serializerKey(model.getKey()), serializerKeyArray(fields));
            }
        });
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     */
    public Boolean hexists(final CacheKeyModel model, final Object field) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.hexists(serializerKey(model.getKey()), serializerKey(field));
            }
        });
    }

    /**
     * 返回哈希表 key 中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     */
    @SuppressWarnings("rawtypes")
    public Map<String,Object> hgetAll(final CacheKeyModel model) {
        return call(new JedisAction<Map<String,Object>>() {
            @Override
            public Map<String,Object> execute(Jedis jedis) {
                Map<byte[], byte[]> data =  jedis.hgetAll(serializerKey(model.getKey()));
                Map<String, Object> result = new HashMap<String, Object>(data.size());
                if (data != null) {
                    for (Map.Entry<byte[], byte[]> e : data.entrySet()) {
                        result.put(deSerializeKey(e.getKey()), deSerializeValue(e.getValue()));
                    }
                }
                return result;
            }
        });
    }

    /**
     * 返回哈希表 key 中所有域的值。
     */
    @SuppressWarnings("rawtypes")
    public List<Object> hvals(CacheKeyModel model) {
        return call(new JedisAction<List<Object>>() {
            @Override
            public List<Object> execute(Jedis jedis) {
                List<byte[]> data = jedis.hvals(serializerKey(model.getKey()));
                return valueListFromBytesList(data);
            }
        });
    }

    /**
     * 返回哈希表 key 中的所有域。
     * 底层实现此方法取名为 hfields 更为合适，在此仅为与底层保持一致
     */
    public Set<String> hkeys(final CacheKeyModel model) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> dataByte = jedis.hkeys(serializerKey(model.getKey()));
                return valueListFromBytesSet(dataByte);
            }
        });
    }

    /**
     * 返回哈希表 key 中域的数量。
     */
    public Long hlen(final CacheKeyModel model) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hlen(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment 。
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * 本操作的值被限制在 64 位(bit)有符号数字表示之内。
     */
    public Long hincrBy(final CacheKeyModel model, Object field, long value) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hincrBy(serializerKey(model.getKey()), serializerValue(field), value);
            }
        });
    }

    /**
     * 为哈希表 key 中的域 field 加上浮点数增量 increment 。
     * 如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
     * 如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
     * 当以下任意一个条件发生时，返回一个错误：
     * 1:域 field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * 2:域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number)
     * HINCRBYFLOAT 命令的详细功能和 INCRBYFLOAT 命令类似，请查看 INCRBYFLOAT 命令获取更多相关信息。
     */
    public Double hincrByFloat(CacheKeyModel model, Object field, double value) {
        return call(new JedisAction<Double>() {
            @Override
            public Double execute(Jedis jedis) {
                return jedis.hincrByFloat(serializerKey(model.getKey()), serializerValue(field), value);
            }
        });
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，
     * 以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 如果 key 不是列表类型，返回一个错误。
     */
    public Integer lindex(final CacheKeyModel model, long index) {
        return call(new JedisAction<Integer>() {
            @Override
            public Integer execute(Jedis jedis) {
                byte[] bytes = jedis.lindex(serializerKey(model.getKey()), index);
                Object data =  deSerializeValue(bytes);
                if (null != data) {
                    expire(model);
                }
                return Integer.parseInt(String.valueOf(data));
            }
        });
    }

    /**
     * 返回列表 key 的长度。
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
     * 如果 key 不是列表类型，返回一个错误。
     */
    public Long llen(final CacheKeyModel model) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.llen(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 移除并返回列表 key 的头元素。
     */
    @SuppressWarnings("unchecked")
    public Long lpop(final CacheKeyModel model) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return Long.parseLong(String.valueOf(deSerializeValue(jedis.lpop(serializerKey(model.getKey())))));
            }
        });
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * <pre>
     * 例子：
     * 获取 list 中所有数据：cache.lrange(listKey, 0, -1);
     * 获取 list 中下标 1 到 3 的数据： cache.lrange(listKey, 1, 3);
     * </pre>
     */
    @SuppressWarnings("rawtypes")
    public List<Object> lrange(final CacheKeyModel model, long start, long end) {
        return call(new JedisAction<List<Object>>() {
            @Override
            public List<Object> execute(Jedis jedis) {
                try {
                    List<byte[]> data = jedis.lrange(serializerKey(model.getKey()), start, end);
                    if (data != null) {
                        return valueListFromBytesList(data);
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage(),e);
                }
                return null;
            }
        });
    }


    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，
     * 对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
     * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     */
    public Long lpush(final CacheKeyModel model, final Object... values) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Long data =  jedis.lpush(serializerKey(model.getKey()), serializerValue(values));
                if (data > 0) {
                    expire(model);
                }
                return data;
            }
        });
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value 。
     * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
     * 关于列表下标的更多信息，请参考 LINDEX 命令。
     */
    public String lset(final CacheKeyModel model, long index, Object value) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                String data =  jedis.lset(serializerKey(model.getKey()), index, serializerValue(value));
                if (null != data) {
                    expire(model);
                }
                return data;
            }
        });
    }

    /**
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
     * count 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     */
    public Long lrem(final CacheKeyModel model, long count, Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.lrem(serializerKey(model.getKey()), count, serializerValue(value));
            }
        });
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * <pre>
     * 例子：
     * 获取 list 中所有数据：cache.lrange(listKey, 0, -1);
     * 获取 list 中下标 1 到 3 的数据： cache.lrange(listKey, 1, 3);
     * </pre>
     *
     * @param model         CacheKeyModel对象
     * @param start			开始位置(0表示第一个元素)
     * @param end			结束位置(-1表示最后一个元素)
     */
    @SuppressWarnings("rawtypes")
    public List lrange(final CacheKeyModel model, final int start, final int end) {
        return call(new JedisAction<List<Object>>(){
            @Override
            public List<Object> execute(Jedis jedis) {
                return valueListFromBytesList(jedis.lrange(serializerKey(model.getKey()), start, end));
            }
        });
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 当 key 不是列表类型时，返回一个错误。
     */
    public String ltrim(final CacheKeyModel model, final int start, final int end) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.ltrim(serializerKey(model.getKey()), start, end);
            }
        });
    }

    /**
     * 移除并返回列表 key 的尾元素。
     */
    @SuppressWarnings("unchecked")
    public String rpop(final CacheKeyModel model) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return String.valueOf(deSerializeValue(jedis.rpop(serializerKey(model.getKey()))));
            }
        });
    }

    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     */
    @SuppressWarnings("unchecked")
    public <T> T rpoplpush(Object srcKey, Object dstKey) {

        return call(new JedisAction<T>(){
            @Override
            public T execute(Jedis jedis) {
                return (T)deSerializeValue(jedis.rpoplpush(serializerKey(srcKey), serializerKey(dstKey)));
            }
        });
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如
     * 对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，
     * 等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     */
    public Long rpush(final CacheKeyModel model, final Object... value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.rpush(serializerKey(model.getKey()), serializerKeyArray(value));
            }
        });
    }

    /**
     * BLPOP 是列表的阻塞式(blocking)弹出原语。
     * 它是 LPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BLPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的头元素。
     *
     * 参考：http://redisdoc.com/list/blpop.html
     * 命令行：BLPOP key [key ...] timeout
     */
    @SuppressWarnings("rawtypes")
    public List<Object> blpop(int timeout, Object... keys) {
        return call(new JedisAction<List<Object>>(){
            @Override
            public List<Object> execute(Jedis jedis) {
                List<byte[]> data =  jedis.blpop(timeout, serializerKeyArray(keys));
                return valueListFromBytesList(data);
            }
        });
    }

    /**
     * BRPOP 是列表的阻塞式(blocking)弹出原语。
     * 它是 RPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
     * 关于阻塞操作的更多信息，请查看 BLPOP 命令， BRPOP 除了弹出元素的位置和 BLPOP 不同之外，其他表现一致。
     *
     * 参考：http://redisdoc.com/list/brpop.html
     * 命令行：BRPOP key [key ...] timeout
     */
    @SuppressWarnings("rawtypes")
    public List<Object> brpop(int timeout, Object... keys) {
        return call(new JedisAction<List<Object>>(){
            @Override
            public List<Object> execute(Jedis jedis) {
                List<byte[]> data =  jedis.brpop(timeout, serializerKeyArray(keys));
                return valueListFromBytesList(data);
            }
        });
    }

    /**
     * 使用客户端向 Redis 服务器发送一个 PING ，如果服务器运作正常的话，会返回一个 PONG 。
     * 通常用于测试与服务器的连接是否仍然生效，或者用于测量延迟值。
     */
    public String ping() {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.ping();
            }
        });
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * 当 key 不是集合类型时，返回一个错误。
     */
    public Long sadd(CacheKeyModel model, Object... values) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.sadd(serializerKey(model.getKey()), serializerKeyArray(values));
            }
        });
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)。
     */
    public Long scard(CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.scard(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 移除并返回集合中的一个随机元素。
     * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
     */
    @SuppressWarnings("unchecked")
    public String spop(CacheKeyModel model) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return String.valueOf(deSerializeValue(jedis.spop(serializerKey(model.getKey()))));
            }
        });
    }

    /**
     * 返回集合 key 中的所有成员。
     * 不存在的 key 被视为空集合。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> smembers(CacheKeyModel model) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.smembers(serializerKey(model.getKey()));
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 判断 member 元素是否集合 key 的成员。
     */
    public Boolean sismember(CacheKeyModel model, Object value) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.sismember(serializerKey(model.getKey()), serializerValue(value));
            }
        });
    }

    /**
     * 返回多个集合的交集，多个集合由 keys 指定
     */
    @SuppressWarnings("rawtypes")
    public Set<String> sinter(Object... keys) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.sinter(serializerKeyArray(keys));
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 返回集合中的一个随机元素。
     */
    @SuppressWarnings("unchecked")
    public String srandmember(CacheKeyModel model) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return String.valueOf(deSerializeValue(jedis.srandmember(serializerKey(model.getKey()))));
            }
        });
    }

    /**
     * 返回集合中的 count 个随机元素。
     * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
     * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。
     * 如果 count 大于等于集合基数，那么返回整个集合。
     * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
     * 该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
     */
    @SuppressWarnings("rawtypes")
    public List<Object> srandmember(CacheKeyModel model, int count) {
        return call(new JedisAction<List<Object>>(){
            @Override
            public List<Object> execute(Jedis jedis) {
                return valueListFromBytesList(jedis.srandmember(serializerKey(model.getKey()),count));
            }
        });
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     */
    public Long srem(CacheKeyModel model, Object... members) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.srem(serializerKey(model.getKey()),serializerKeyArray(members));
            }
        });
    }

    /**
     * 返回多个集合的并集，多个集合由 keys 指定
     * 不存在的 key 被视为空集。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> sunion(Object... keys) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.sunion(serializerKeyArray(keys));
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
     * 不存在的 key 被视为空集。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> sdiff(Object... keys) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.sdiff(serializerKeyArray(keys));
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，
     * 并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     */
    public Long zadd(CacheKeyModel model, double score, Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zadd(serializerKey(model.getKey()), score, serializerValue(value));
            }
        });
    }

    public Long zadd(CacheKeyModel model, Map<Object, Double> scoreMembers) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Map<byte[], Double> para = new HashMap<byte[], Double>();
                for (Map.Entry<Object, Double> e : scoreMembers.entrySet()) {
                    para.put(serializerKey(model.getKey()), e.getValue());    // valueToBytes is important
                }
                return jedis.zadd(serializerKey(model.getKey()), para);
            }
        });
    }

    /**
     * 返回有序集 key 的基数。
     */
    public Long zcard(CacheKeyModel model) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zcard(serializerKey(model.getKey()));
            }
        });
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     * 关于参数 min 和 max 的详细使用方法，请参考 ZRANGEBYSCORE 命令。
     */
    public Long zcount(CacheKeyModel model, double min, double max) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zcount(serializerKey(model.getKey()), min, max);
            }
        });
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     */
    public Double zincrby(CacheKeyModel model, double score, Object member) {
        return call(new JedisAction<Double>(){
            @Override
            public Double execute(Jedis jedis) {
                return jedis.zincrby(serializerKey(model.getKey()), score, serializerValue(member));
            }
        });
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
     * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> zrange(CacheKeyModel model, long start, long end) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.zrange(serializerKey(model.getKey()), start, end);
                return valueListFromBytesSet(data);
            }
        });

    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递减(从大到小)来排列。
     * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> zrevrange(CacheKeyModel model, long start, long end) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.zrevrange(serializerKey(model.getKey()), start, end);
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 有序集成员按 score 值递增(从小到大)次序排列。
     */
    @SuppressWarnings("rawtypes")
    public Set<String> zrangeByScore(CacheKeyModel model, double min, double max) {
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                Set<byte[]> data = jedis.zrangeByScore(serializerKey(model.getKey()), min, max);
                return valueListFromBytesSet(data);
            }
        });
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
     * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
     * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
     */
    public Long zrank(CacheKeyModel model, Object member) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrank(serializerKey(model.getKey()), serializerValue(member));
            }
        });
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
     * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
     * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
     */
    public Long zrevrank(CacheKeyModel model, Object member) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrevrank(serializerKey(model.getKey()), serializerValue(member));
            }
        });
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     */
    public Long zrem(CacheKeyModel model, Object... members) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrem(serializerKey(model.getKey()), serializerValueArray(members));
            }
        });
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值。
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
     */
    public Double zscore(CacheKeyModel model, Object members) {
        return call(new JedisAction<Double>(){
            @Override
            public Double execute(Jedis jedis) {
                return jedis.zscore(serializerKey(model.getKey()), serializerValue(members));
            }
        });
    }

    /**
     * 删除当前 db 所有数据
     */
    public String flushDB() {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.flushDB();
            }
        });
    }

    /**
     * 删除所有 db 的所有数据
     */
    public String flushAll() {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                return jedis.flushAll();
            }
        });
    }

    /**
     * subscribe channel [channel …] 订阅一个或多个频道 <br/>
     * PS：<br/>
     *    取消订阅在 jedisPubSub 中的 unsubscribe 方法。<br/>
     *    重要：订阅后代码会阻塞监听发布的内容<br/>
     */
    public String subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                try {
                    jedis.subscribe(jedisPubSub, channels);
                    return "success";
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    return "fail";
                }

            }
        });
    }

    /**
     * subscribe channel [channel …] 订阅一个或多个频道<br/>
     * PS：<br/>
     *    取消订阅在 jedisPubSub 中的 unsubscribe 方法。<br/>
     */
    public JedisPubSub subscribeThread(final JedisPubSub jedisPubSub, final String... channels) {
        new Thread(() -> subscribe(jedisPubSub, channels)).start();
        return jedisPubSub;
    }

    /**
     * psubscribe pattern [pattern …] 订阅给定模式相匹配的所有频道<br/>
     * PS：<br/>
     *     取消订阅在 jedisPubSub 中的 punsubscribe 方法。<br/>
     *     重要：订阅后代码会阻塞监听发布的内容<br/>
     */
    public String psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        return call(new JedisAction<String>(){
            @Override
            public String execute(Jedis jedis) {
                try {
                    jedis.psubscribe(jedisPubSub, patterns);
                    return "success";
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    return "fail";
                }
            }
        });
    }

    /**
     * psubscribe pattern [pattern …] 订阅给定模式相匹配的所有频道<br/>
     * PS：<br/>
     *     取消订阅在 jedisPubSub 中的 punsubscribe 方法。<br/>
     */
    public JedisPubSub psubscribeThread(final JedisPubSub jedisPubSub, final String... patterns) {
        new Thread(() -> psubscribe(jedisPubSub, patterns)).start();
        return jedisPubSub;
    }

    /**
     * publish channel message 给指定的频道发消息
     */
    public Long publish(final String channel, final String message) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.publish(channel, message);
            }
        });
    }

}
