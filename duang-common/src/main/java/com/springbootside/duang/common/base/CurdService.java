package com.springbootside.duang.common.base;

import com.springbootside.duang.common.utils.ToolsKit;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class CurdService<T> implements ICurdService<T> {

    /** 取泛型对象类型
     * @return
     */
    protected Class<T> getGenericTypeClass() {
        return (Class<T>) ToolsKit.getSuperClassGenericType(getClass(), 0);
    }

    @Override
    public T findById(Serializable id) {
        return null;
    }

    @Override
    public T save(T entity) {
        Class<T> tClass = getGenericTypeClass();
        System.out.println("@@@@@@@: " + tClass);
        return null;
    }

    @Override
    public List<T> search() {
        return null;
    }
}
