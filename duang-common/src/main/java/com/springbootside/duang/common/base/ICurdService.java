package com.springbootside.duang.common.base;

import com.springbootside.duang.common.utils.ToolsKit;

import java.io.Serializable;
import java.util.List;

public interface ICurdService<T> {

    /**
     * 根据ID字段查找记录
     * @param id id主键
     * @return 记录对象
     */
    T findById(Serializable id);

    /**
     * 新增或修改后保存对象
     * @param entity 待持久化的对象
     * @return
     */
    T save(T entity);

    List<T> search();

}
