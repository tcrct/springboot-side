package com.springbootside.duang.common.dao;

import java.io.Serializable;

/**
 * 定义 SQL DAO 的公用方法
 * @param <T> 实体类泛型
 *
 * @author Laotang
 * @since 1.0
 */
public interface SqlDao<T> {

    /**保存对象*/
    T save(T obj);

    /**根据ID查找对象记录*/
    T findById(Serializable id);
}
