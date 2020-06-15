package com.springbootside.duang.db.curd;

import com.springbootside.duang.db.dto.PageDto;
import com.springbootside.duang.db.dto.SearchDto;
import com.springbootside.duang.db.dto.SearchListDto;
import com.springbootside.duang.db.model.IdEntity;
import com.springbootside.duang.db.utils.DbKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.ConstantEnum;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CurdService<T> implements ICurdService<T> {

    private static final Integer pageNo = 1;
    private static final Integer pageSize = 20;

    @Autowired
    private SQLManager manager;

    /** 取泛型对象类型
     * @return
     */
    protected Class<T> getGenericTypeClass() {
        return (Class<T>) DbKit.getSuperClassGenericType(getClass(), 0);
    }

    /**
     * 根据ID查找对象
     * @param id id主键
     * @return
     */
    @Override
    public T findById(Serializable id) {
        return manager.unique(getGenericTypeClass(), id);
    }

    /**
     * 保存操作
     * @param entity 待持久化的对象
     * @return
     */
    @Override
    public T save(T entity) {

        Class<T> genericTypeClass = getGenericTypeClass();
        IdEntity idEntity = (IdEntity) entity;
        if (null == idEntity.getId() || "".equals(idEntity.getId())) {
            manager.insert(genericTypeClass, entity);
        } else {
            manager.updateAll(genericTypeClass, entity);
        }
        return null;
    }

    /**
     * 根据条件搜索记录，返回分页对象
     * @param searchDto 搜索条件对象
     * @return PageDto
     */
    @Override
    public PageDto<T> search(SearchListDto searchListDto) {
        Class<T> genericTypeClass = getGenericTypeClass();
        PageDto<T> pageDto = new PageDto<>();
        String sqlId = searchListDto.getKey();
        List<T> resultList = null;
        Query<T> query = manager.query(genericTypeClass);
        if (null != sqlId) {
            resultList = manager.select(sqlId, genericTypeClass, searchListDto.toMap());
        } else {
            query = DbKit.createQueryCondition(query, searchListDto);
            query = query.orderBy(searchListDto.toOrderByStr());
            if (null != searchListDto.getOrderByDtoList()) {
                    query.orderBy(searchListDto.toOrderByStr());
            }
            if (null != searchListDto.getGroupByList()) {
                query.groupBy(searchListDto.toGroupByStr());
            }
            query.limit(searchListDto.getPageNo(), searchListDto.getPageSize());
            resultList = (null == searchListDto.getFieldList()) ? query.select() :
                    query.select(searchListDto.getFieldList().toArray(new String[]{}));
        }
        pageDto.setAutoCount(true);
        pageDto.setResult(resultList);
        pageDto.setTotalCount(query.count());
        return pageDto;
    }
}
