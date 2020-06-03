package com.springbootside.duang.redis.core;

public class CacheException extends RuntimeException {

    public CacheException(String errMessage) {
           super(errMessage);
    }

    public CacheException(String errMessage, Throwable e) {
        super(errMessage, e);
    }
}
