package com.springbootside.duang.redis;

import com.springbootside.duang.redis.core.CacheException;
import com.springbootside.duang.redis.core.CacheKeyModel;
import com.springbootside.duang.redis.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
            Jedis jedis = jedisThreadLocal.get();
            if (null != jedis) {
                isJedisThreadLocal = true;
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

    public void close() {
        jedisPool.close();
    }

    private void close(Jedis jedis) {
        if (jedisThreadLocal.get() == null && jedis != null) {
            jedis.close();
            jedisThreadLocal.remove();
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
            if (!isJedisThreadLocal) {
                close(jedis);
            }
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

    protected byte[][] serializerKeyArray(Object... keys) {
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
        return serializer.valueToBytes(value);
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
    protected String deSerializeValue(byte[] bytes) {
        return String.valueOf(serializer.valueFromBytes(bytes));
    }

    protected List<String> valueListFromBytesList(List<byte[]> data) {
        List<String> result = new ArrayList<>(data.size());
        for (byte[] d : data) {
            result.add(String.valueOf(deSerializeValue(d)));
        }
        return result;
    }

    protected Set<String> valueListFromBytesSet(Set<byte[]> data) {
        Set<String> result = new HashSet<>(data.size());
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
    public Boolean hmset(final CacheKeyModel options, final Map<String, String> values) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                String isok = "";
                if(null != values){
                    Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(values.size());
                    for (Iterator<Map.Entry<String,String>> it = values.entrySet().iterator(); it.hasNext(); ){
                        Map.Entry<String,String> entry = it.next();
                        map.put(serializerKey(entry.getKey()), serializerValue(entry.getValue()));
                    }
                    isok = jedis.hmset(serializerKey(options.getKey()), map);
                    boolean isOk = "OK".equalsIgnoreCase(isok);
                    if(isOk) {
                        expire(options);
                    }
                }
                return false;
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
    public String hget(final CacheKeyModel model, final String field) {
        return call(new JedisAction<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(model.getKey(),  field);
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
    public List<String> hmget(final CacheKeyModel model, final String... fields) {
        return call(new JedisAction<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
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
    public Map<String,String> hmgetToMap(final CacheKeyModel model, final String... fields) {
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String, String> execute(Jedis jedis) {
                List<String> byteList = jedis.hmget(model.getKey(), fields);
                int size  = byteList.size();
                Map<String,String> map = new HashMap<>(size+1);
                for (int i = 0; i < size; i ++) {
                    if(null != byteList.get(i)) {
                        map.put(fields[i], byteList.get(i));
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
    public Map<String,String> hgetAll(final CacheKeyModel model) {
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String,String> execute(Jedis jedis) {
                Map<byte[], byte[]> data =  jedis.hgetAll(serializerKey(model.getKey()));
                Map<String, String> result = new HashMap<String, String>(data.size());
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
    public List<String> hvals(CacheKeyModel model) {
        return call(new JedisAction<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
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
    public String lindex(final CacheKeyModel model, long index) {
        return call(new JedisAction<String>() {
            @Override
            public String execute(Jedis jedis) {
                byte[] bytes = jedis.lindex(serializerKey(model.getKey()), index);
                String data =  deSerializeValue(bytes);
                if (null != data) {
                    expire(model);
                }
                return data;
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
                return Long.parseLong(deSerializeValue(jedis.lpop(serializerKey(model.getKey()))));
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
    public List<String> lrange(final CacheKeyModel model, long start, long end) {
        return call(new JedisAction<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
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
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如
     * 对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，
     * 等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     */
    public Long rpush(final CacheKeyModel model, final Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.rpush(serializerKey(model.getKey()), serializerValue(value));
            }
        });
    }


}
