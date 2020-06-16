package com.springbootside.duang.redis.crud;

public interface ICurdCacheService<T> {

    /**
     * 保存到缓存
     * @param entity
     * @return
     */
    int save(T entity);

    /**
     * 根据key值，查找缓存记录
     * @param key 缓存key
     * @return
     */
    T findById(String key);

    /**
     * 根据key值，删除缓存记录
     * @param key 缓存key
     * @return
     */
    int deleteById(String key);

}
