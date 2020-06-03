package com.springbootside.duang.redis;

import com.springbootside.duang.redis.serializer.FstSerializer;
import com.springbootside.duang.redis.serializer.ISerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 单节点Redis配置信息对象
 *
 * @author Laotang
 * @since 1.0
 */
public class RedisConfig {
    /**host*/
    private String host;
    /**端口*/
    private Integer port = null;
    /**链接超时*/
    private Integer timeout = null;
    /**密码*/
    private String password = null;
    /**数据库*/
    private Integer database = null;
    /**名称*/
    private String clientName = null;
    /**kv值的序列化对象*/
    private ISerializer serializer = null;

    private RedisConfig(String host, Integer port, Integer timeout, String password, Integer database, String clientName, ISerializer serializer) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
        this.clientName = clientName;
        this.serializer = serializer;
    }

    /**
     * 构建
     */
    public static class Builder {

        private String host;
        private Integer port = null;
        private Integer timeout = null;
        private String password = null;
        private Integer database = null;
        private String clientName = null;
        private ISerializer serializer = null;

        public Builder host(String host) {
            this.host = host;
            return this;
        }
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public Builder database(int database) {
            this.database = database;
            return this;
        }
        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }
        public Builder serializer(ISerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public RedisConfig build() {
            if (serializer == null) {
                serializer = FstSerializer.me;
            }
            return new RedisConfig(host, port, timeout, password, database, clientName, serializer);
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getPassword() {
        return password;
    }

    public Integer getDatabase() {
        return database;
    }

    public String getClientName() {
        return clientName;
    }

    public ISerializer getSerializer() {
        return serializer;
    }

    /**
     * 取JedisPool对象
     * @return JedisPool
     */
    public JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool jedisPool = null;
        if (port != null && timeout != null && database != null && clientName != null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database, clientName);
        }
        else if (port != null && timeout != null && database != null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        }
        else if (port != null && timeout != null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        }
        else if (port != null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port);
        }
        else {
            jedisPool = new JedisPool(jedisPoolConfig, host);
        }
        return jedisPool;
    }
    
}
