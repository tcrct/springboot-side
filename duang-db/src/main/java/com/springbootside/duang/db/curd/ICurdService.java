package com.springbootside.duang.db.curd;

import com.springbootside.duang.db.dto.PageDto;
import com.springbootside.duang.db.dto.SearchDto;
import com.springbootside.duang.db.dto.SearchListDto;

import java.io.Serializable;

/**
 * CURD方法服务类
 *
 * @param <T> 要操作的泛型对象
 *
 * @author Laotang
 * @since 1.0
 */
public interface ICurdService<T> {

    /**
     * 根据ID字段查找记录
     * @param id id主键
     * @return 记录对象
     */
    T findById(Serializable id);

    /**
     * 根据ID，逻辑删除记录(将表中status字段值改为0)
     * @param id 待删除的记录ID
     * @return 成功删除返回受影响的行数
     */
    Integer deleteById(Serializable id);

    /**
     * 新增或修改后保存对象
     * @param entity 待持久化的对象
     * @return 受影响的行数
     */
    Integer save(T entity);

    /**
     * 根据条件对象进行搜索
     *
     * @return 分页对象
     */
    PageDto<T> search(SearchListDto searchListDto);

}
