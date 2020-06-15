package com.springbootside.duang.db.curd;

import com.springbootside.duang.db.dto.PageDto;
import com.springbootside.duang.db.dto.SearchDto;
import com.springbootside.duang.db.dto.SearchListDto;
import com.springbootside.duang.db.model.IdEntity;
import com.springbootside.duang.db.utils.DbKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
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
        String sqlId = searchListDto.getKey();
        Map<String,Object> paramMap =  searchListDto.toMap();
        List<SearchDto> searchDtoList = searchListDto.getSearchDtoList();
        PageQuery<T> pageQuery = new PageQuery<T>(pageNo, pageSize, searchListDto.toMap());
        pageQuery.setOrderBy(searchListDto.toOrderByStr());
        if (null != sqlId) {
            pageQuery = manager.pageQuery(sqlId, genericTypeClass, pageQuery);
        } else {
            String sql = searchListDto.toSql(manager.getDbStyle().getNameConversion().getTableName(genericTypeClass));
            Query<T> query = manager.query(genericTypeClass);
            DbKit.createQueryCondition(query, searchListDto);

                    .orderBy()
                    .getSql().toString();
            pageQuery = manager.execute(new SQLReady(sql), genericTypeClass, pageQuery);
        }

        return DbKit.toPageDto(pageQuery);
    }
}
