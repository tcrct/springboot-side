package com.springbootside.duang.redis.crud;

/**
 * 通用CURD Cache层
 *
 * @author Laotang
 * @since 1.0
 */
public class CrudCacheService<T> implements ICurdCacheService<T> {

    @Override
    public int save(T entity) {
        return 0;
    }

    @Override
    public T findById(String key) {
        return null;
    }

    @Override
    public int deleteById(String key) {
        return 0;
    }
}
