package com.springbootside.duang.db.dao;

import cn.hutool.db.sql.Query;
import com.springbootside.duang.common.utils.ToolsKit;
import org.beetl.sql.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.List;

/**
 * 定义 SQL DAO 的公用方法
 * @param <T> 实体类泛型
 *
 * @author Laotang
 * @since 1.0
 */
public interface Dao<T> extends BaseMapper<T> {

    default Class<?> getEntityClass() {
        return ToolsKit.getSuperClassGenericType(getClass(), 0);
    }

    /**保存对象*/
    T save(T obj);

    /**根据ID查找对象记录
     * @param id 主键ID
     */
    T findById(Serializable id);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param query 实体对象封装操作类（可以为 null）
     */
    List<T> findList(Query query);

    /**
     * 根据 ID 删除
     * @param id 主键ID
     * @return 删除成功返回true
     */
    boolean deleteById(Serializable id);
}
