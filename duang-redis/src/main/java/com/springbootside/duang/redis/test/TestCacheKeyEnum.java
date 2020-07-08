package com.springbootside.duang.redis.test;

import com.springbootside.duang.redis.core.ICacheKeyEnums;

public enum TestCacheKeyEnum implements ICacheKeyEnums {

    MER_ID("zat:mpay:userid:", ICacheKeyEnums.DEFAULT_TTL, "用户ID");


    private String keyPrefix;
    private int  ttl;
    private String keyDesc;
    private TestCacheKeyEnum(String keyPrefix, int ttl, String keyDesc) {
        this.keyPrefix = keyPrefix;
        this.ttl = ttl;
        this.keyDesc = keyDesc;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public int getKeyTTL() {
        return ttl;
    }

    public String getKeyDesc() {
        return keyDesc;
    }
}
